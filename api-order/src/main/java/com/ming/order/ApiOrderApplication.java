package com.ming.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.ming.order.mapper")
@EnableDubbo
@SpringBootApplication
public class ApiOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiOrderApplication.class, args);
        System.out.println("成功开启订单服务");
    }
}
