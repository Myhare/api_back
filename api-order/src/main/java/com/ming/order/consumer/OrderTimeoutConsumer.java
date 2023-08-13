package com.ming.order.consumer;

import com.ming.apiCommon.dubbo.ApiBackendService;
import com.ming.order.constant.MQPrefixConst;
import com.ming.order.enums.OrderStatusEnum;
import com.ming.order.pojo.Order;
import com.ming.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 订单超时死信队列
 */
@Log4j2
@Component
@RabbitListener(queues = MQPrefixConst.ORDER_DLX_QUEUE)
public class OrderTimeoutConsumer {

    @DubboReference
    private ApiBackendService apiBackendService;

    @Resource
    private OrderService orderService;

    /**
     * 订单超时队列
     */
    @Transactional(rollbackFor = Exception.class)
    @RabbitHandler
    public void orderTimeout(Order tOrder, Message message, Channel channel) throws IOException {
        // 获取最新的订单状态
        Order order = orderService.getById(tOrder.getId());
        // 订单已支付，直接返回ack消息
        if (order.getStatus().equals(OrderStatusEnum.PAYED.getValue())){
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // false表示只确认当前一条消息
            return;
        }
        // 订单未支付，回退库存的状态，将订单的状态修改成已过期
        Integer count = order.getCount();
        try {
            // 回滚库存
            boolean isOk = apiBackendService.rollBackStock(order.getInterfaceId(), count);
            if (!isOk){
                log.error("回滚库存失败");
                // 执行basicNack会组织后面代码的运行
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            }
            // 更新订单状态
            order.setStatus(OrderStatusEnum.TIMEOUT.getValue());
            orderService.updateById(order);
            // 返回成功信息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }catch (Exception e){
            // TODO 发送消息失败，可以通过邮箱通知技术人员
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
