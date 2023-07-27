package com.ming.web;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.ming.web.mapper")
@SpringBootApplication
@EnableDubbo
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class ,args);
        System.out.println("成功进入-web服务");
    }
}
