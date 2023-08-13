package com.ming.order.constant;

public class MQPrefixConst {

    /**
     * 订单支付交换机
     */
    public static final String ORDER_PAY_EXCHANGE ="order_pay_exchange";
    /**
     * 订单支付队列
     */
    public static final String ORDER_PAY_QUEUE = "order_pay_queue";
    /**
     * 订单支付路由key
     */
    public static final String ROUTING_KEY_ORDER_PAY ="routing.order.pay";

    /**
     * 死信队列
     */
    public static final String ORDER_DLX_QUEUE = "order_dlx_queue";
    /**
     * 死信交换机
     */
    public static final String ORDER_DLX_EXCHANGE = "order_dlx_exchange";
    /**
     * 死信队列路由key
     */
    public static final String ROUTING_KEY_DLX_ORDER_PAY ="routing.dlx.order.pay";

}
