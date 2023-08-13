package com.ming.order.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ming.apiCommon.dubbo.ApiBackendService;
import com.ming.apiCommon.model.vo.PageResult;
import com.ming.apiCommon.model.vo.ResponseResult;
import com.ming.order.enums.OrderStatusEnum;
import com.ming.order.pojo.Order;
import com.ming.order.service.OrderService;
import com.ming.order.vo.IdVO;
import com.ming.order.vo.OrderAddVO;
import com.ming.order.vo.OrderQueryInfoVO;
import com.ming.order.vo.OrderVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @DubboReference
    private ApiBackendService apiBackendService;

    /**
     * 添加一个订单
     * @return
     */
    @PostMapping("/addOrder")
    public ResponseResult<String> addOrder(@RequestBody OrderAddVO orderAddVO, HttpServletRequest httpServletRequest){
        orderService.addOrder(orderAddVO, httpServletRequest);
        return ResponseResult.ok();
    }


    /**
     * 查询用户的订单列表
     */
    @GetMapping("/list")
    public ResponseResult<PageResult<OrderVO>> listOrder(OrderQueryInfoVO queryInfoVO, HttpServletRequest request){
        return ResponseResult.ok(orderService.listOrder(queryInfoVO, request));
    }

    /**
     * 订单支付
     */
    @PostMapping("/orderPay")
    public ResponseResult<String> orderPay(@RequestBody IdVO idVO){
        orderService.addInvokeCount(idVO);
        return ResponseResult.ok();
    }
}
