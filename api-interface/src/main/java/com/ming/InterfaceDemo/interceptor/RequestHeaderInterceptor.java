package com.ming.InterfaceDemo.interceptor;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestHeaderInterceptor implements HandlerInterceptor {

    // 判断请求是不是从网关发送的,如果不是就进行拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String source = request.getHeader("source");
        return StringUtils.isNotEmpty(source) && "1940307627".equals(source);
    }
}
