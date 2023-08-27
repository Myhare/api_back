package com.ming.order.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.apiCommon.dubbo.ApiBackendService;
import com.ming.apiCommon.dubbo.InnerInterfaceInfoService;
import com.ming.apiCommon.model.entity.InterfaceInfo;
import com.ming.apiCommon.model.enums.ResultCodeEnum;
import com.ming.apiCommon.model.vo.PageResult;
import com.ming.apiCommon.model.vo.UserVO;
import com.ming.apiCommon.utils.CommonUtil;
import com.ming.apiCommon.utils.JwtUtil;
import com.ming.order.enums.OrderStatusEnum;
import com.ming.order.exception.BusinessException;
import com.ming.order.mapper.OrderMapper;
import com.ming.order.pojo.Order;
import com.ming.order.service.OrderService;
import com.ming.order.vo.IdVO;
import com.ming.order.vo.OrderAddVO;
import com.ming.order.vo.OrderQueryInfoVO;
import com.ming.order.vo.OrderVO;
import io.jsonwebtoken.Claims;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ming.apiCommon.constant.RequestHeaderConstant.TOKEN_HEADER;
import static com.ming.order.constant.MQPrefixConst.ORDER_PAY_EXCHANGE;
import static com.ming.order.constant.MQPrefixConst.ROUTING_KEY_ORDER_PAY;

/**
 * 订单服务
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private ApiBackendService apiBackendService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    // 添加一个订单
    @Override
    @GlobalTransactional
    public void addOrder(OrderAddVO orderAddVO, HttpServletRequest request) {
        // 校验参数
        if (Objects.isNull(orderAddVO)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Long interfaceId = orderAddVO.getInterfaceId();
        Double charging = orderAddVO.getCharging();
        Integer count = orderAddVO.getCount();
        BigDecimal totalAmount = orderAddVO.getTotalAmount();
        if (interfaceId == null || count ==null || totalAmount == null || charging == null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        if (count <= 0 || totalAmount.compareTo(new BigDecimal(0)) < 0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        // 获取登录用户
        UserVO userVO = getUserVoByRequest(request);
        // 获取接口信息
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceById(interfaceId);
        if (interfaceInfo == null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "接口不存在");
        }
        // 后端手动校验订单的价格
        double temp = charging * count;
        BigDecimal bigDecimal = new BigDecimal(temp);
        double price = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (price != totalAmount.doubleValue()){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "价格异常");
        }

        // 判断库存是否足够
        String stockStr = apiBackendService.getStockByInterfaceId(interfaceId);
        // 如果不是一个数字，说明这个接口库存已经修改成了无限制
        if (!CommonUtil.isInteger(stockStr)){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "接口为无限制");
        }
        int stock = Integer.parseInt(stockStr);
        if (stock < count){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "库存不足");
        }
        // 远程修改库存数量
        boolean isOk = apiBackendService.reduceStock(interfaceId, count);
        if (!isOk){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "扣减库存失败，系统繁忙");
        }
        // 数据库添加订单数据
        Order order = new Order();
        // 生成订单号
        order.setOrderSn(generateOrderNum(userVO.getId()));
        order.setUserId(userVO.getId());
        order.setInterfaceId(interfaceId);
        order.setCount(count);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatusEnum.UN_PAY.getValue());
        order.setCharging(charging);
        this.save(order);
        // 发送向交换机发送消息
        String finalMessageId = IdUtil.simpleUUID();
        rabbitTemplate.convertAndSend(ORDER_PAY_EXCHANGE,ROUTING_KEY_ORDER_PAY, order, message -> {
            // 对要发送的信息设置唯一的id
            MessageProperties messageProperties = message.getMessageProperties();
            //生成全局唯一id
            messageProperties.setMessageId(finalMessageId);
            //设置消息的有效时间
            messageProperties.setContentEncoding("utf-8");
            return message;
        });
    }

    // 分页查询登录用户的账单列表
    @Override
    public PageResult<OrderVO> listOrder(OrderQueryInfoVO queryInfoVO, HttpServletRequest request) {
        UserVO loginUserVO = getUserVoByRequest(request);
        Page<Order> page = new Page<>(queryInfoVO.getLimitCurrent(), queryInfoVO.getPageSize());

        Page<Order> orderPage = orderMapper.selectPage(page, new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, loginUserVO.getId())
                .eq(Order::getStatus, queryInfoVO.getOrderStatus())
        );

        // 通过订单中的接口查看接口详细信息
        List<Order> orderList = orderPage.getRecords();
        List<Long> orderIdList = orderList.stream().map(Order::getInterfaceId).collect(Collectors.toList());
        // 远程获取接口列表
        List<InterfaceInfo> interfaceInfoList = innerInterfaceInfoService.listInterfaceByIdList(orderIdList);
        // 封装信息
        Map<Long, List<InterfaceInfo>> interfaceIdMap = interfaceInfoList.stream().collect(Collectors.groupingBy(InterfaceInfo::getId));

        List<OrderVO> orderVOList = orderList.stream().map(order -> {
            InterfaceInfo interfaceInfo = interfaceIdMap.get(order.getInterfaceId()).get(0);
            return OrderVO.builder()
                    .orderId(order.getId())
                    .interfaceId(interfaceInfo.getId())
                    .orderNumber(order.getOrderSn())
                    .interfaceName(interfaceInfo.getName())
                    .interfaceDesc(interfaceInfo.getDescription())
                    .count(order.getCount())
                    .charging(order.getCharging())
                    .totalAmount(order.getTotalAmount().doubleValue())
                    .status(order.getStatus())
                    .createTime(order.getCreateTime())
                    .expirationTime(DateUtil.offset(order.getCreateTime(), DateField.MINUTE, 30))
                    .build();
        }).collect(Collectors.toList());
        return new PageResult<OrderVO>(orderVOList, (int) orderPage.getTotal());
    }

    @Override
    public void addInvokeCount(IdVO idVO) {
        // TODO 添加分布式事务
        Order order = this.getById(idVO.getId());
        order.setStatus(OrderStatusEnum.PAYED.getValue());
        Long userId = order.getUserId();
        Long interfaceId = order.getInterfaceId();
        this.updateById(order);
        // 当前接口的调用次数添加到数据库中
        apiBackendService.addInvokeCount(userId, interfaceId, order.getCount());
    }

    // 回滚库存
    @Override
    @GlobalTransactional
    public void delOrder(Long orderId) {
        if (orderId == null || orderId < 1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Order order = orderMapper.selectById(orderId);
        // 判断订单的状态，如果是未支付，就回滚订单
        if (order.getStatus().equals(OrderStatusEnum.UN_PAY.getValue())){
            backStock(order.getInterfaceId(), order.getCount());
        }
        // 删除订单
        this.removeById(orderId);
    }

    /**
     * 回滚库存
     * @param interfaceId 接口id
     * @param count       回滚的数量
     */
    private void backStock(Long interfaceId, Integer count){
        // 回滚库存
        boolean isOk = apiBackendService.rollBackStock(interfaceId, count);
        if (!isOk){
            log.error("回滚库存失败");
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "系统繁忙");
        }
    }

    /**
     * 获取登录用户
     */
    private UserVO getUserVoByRequest(HttpServletRequest request){
        // 获取登录用户
        String token = request.getHeader(TOKEN_HEADER);
        UserVO userVO = null;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userVO = JSONUtil.toBean(claims.getSubject(), UserVO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"token失效");
        }
        if (userVO == null){
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        return userVO;
    }


    /**
     * 生成订单号
     *
     * @return
     */
    private String generateOrderNum(Long userId) {
        String timeId = IdWorker.getTimeId();
        String substring = timeId.substring(0, timeId.length() - 15);
        return substring + RandomUtil.randomNumbers(5) + userId;
    }
}




