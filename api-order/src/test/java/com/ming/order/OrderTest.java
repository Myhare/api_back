package com.ming.order;

import cn.hutool.core.util.IdUtil;
import com.ming.order.pojo.Order;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.ming.order.constant.MQPrefixConst.*;

@SpringBootTest
public class OrderTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testOrder(){
        Order order = new Order();
        order.setId(1L);
        order.setUserId(2L);
        order.setCount(9999);
        String finalMessageId = IdUtil.simpleUUID();
        rabbitTemplate.convertAndSend(ORDER_PAY_EXCHANGE,ROUTING_KEY_ORDER_PAY, order, message -> {
            //设置有效时间，如果消息不被消费，进入死信队列
            message.getMessageProperties().setExpiration("1000");
            // 对要发送的信息设置唯一的id
            MessageProperties messageProperties = message.getMessageProperties();
            //生成全局唯一id
            messageProperties.setMessageId(finalMessageId);
            //设置消息的有效时间
//            message.getMessageProperties().setExpiration("1000*60");
            messageProperties.setContentEncoding("utf-8");
            return message;
        });
    }

}
