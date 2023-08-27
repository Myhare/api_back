package com.ming.order.service;

import com.ming.apiCommon.model.vo.PageResult;
import com.ming.order.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.order.vo.IdVO;
import com.ming.order.vo.OrderAddVO;
import com.ming.order.vo.OrderQueryInfoVO;
import com.ming.order.vo.OrderVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单服务
 * @author Ming
 */
public interface OrderService extends IService<Order> {

    /**
     * 添加一个订单
     * @param orderAddVO
     * @param httpServletRequest
     */
    void addOrder(OrderAddVO orderAddVO, HttpServletRequest httpServletRequest);

    /**
     * 分页查询登录用户端口
     * @param queryInfoVO
     * @return
     */
    PageResult<OrderVO> listOrder(OrderQueryInfoVO queryInfoVO, HttpServletRequest request);

    /**
     * 给用户添加调用接口的次数
     */
    void addInvokeCount(IdVO idVO);

    /**
     * 回滚库存
     * @param id 库存id
     */
    void delOrder(Long orderId);
}
