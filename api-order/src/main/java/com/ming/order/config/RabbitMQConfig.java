package com.ming.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.ming.order.constant.MQPrefixConst.*;

@Configuration
public class RabbitMQConfig {

    /*
        订单过期流程： 消息（创建的订单号）---》
        发送到订单死信队列，不消费（设置过期时间）---》
        （超过设定的过期时间）根据ORDER_DEAD_LETTER_QUEUE_KEY路由死信交换机 ---》
        重新消费，根据ORDER_DEAD_LETTER_ROUTING_KEY转发到转发队列（取出消息订单号查找订单，假如仍然未支付就取消订单）---》end
     */

    // public static final Integer HALF_HOUR = 1000 * 10; // 10秒，测试使用
    // 订单有效时间半个小时
    public static final Integer HALF_HOUR = 1000 * 60 * 30;

    /**
     * 死信交换机
     */
    @Bean
    public Exchange order_dlx_exchange(){
        return new DirectExchange(ORDER_DLX_EXCHANGE, true, false);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue order_dlx_queue(){
        return new Queue(ORDER_DLX_QUEUE, true, false, false);
    }

    /**
     * 绑定死信交换机
     */
    @Bean
    public Binding binding_queue_dlx_order_pay(){
        return new Binding(ORDER_DLX_QUEUE, Binding.DestinationType.QUEUE,
                ORDER_DLX_EXCHANGE, ROUTING_KEY_DLX_ORDER_PAY, null);
    }

    /**
     * 订单支付交换机
     */
    @Bean
    public Exchange order_pay_exchange(){
        return new DirectExchange(ORDER_PAY_EXCHANGE, true, false);
    }

    /**
     * 订单支付队列
     */
    @Bean
    public Queue order_pay_queue(){
        // 设置死信队列
        Map<String, Object> args = new HashMap<>(3);
        // 死信交换机
        args.put("x-dead-letter-exchange", ORDER_DLX_EXCHANGE);
        // 通过key指定发送的队列
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DLX_ORDER_PAY);
        // 设置超时时间,超过下面的时间就会进入死信队列
        args.put("x-message-ttl", HALF_HOUR);
        return new Queue(ORDER_PAY_QUEUE, true, false, false, args);
    }

    /**
     * 绑定订单交换机和队列
     */
    @Bean
    public Binding binding_queue_order_pay(){
        return new Binding(ORDER_PAY_QUEUE, Binding.DestinationType.QUEUE,
                ORDER_PAY_EXCHANGE, ROUTING_KEY_ORDER_PAY, null);
    }

}
