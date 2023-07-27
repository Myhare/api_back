package com.ming.InterfaceDemo.config;

import com.ming.InterfaceDemo.interceptor.RequestHeaderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private RequestHeaderInterceptor requestHeaderInterceptor;

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加流量染色拦截器
        registry.addInterceptor(requestHeaderInterceptor);
    }
}
