package com.ming.web;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.ming.web.mapper")
@SpringBootApplication
@EnableDubbo
@EnableScheduling       // 开启定时任务
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class ,args);
        System.out.println("成功进入-web服务");
    }
}
