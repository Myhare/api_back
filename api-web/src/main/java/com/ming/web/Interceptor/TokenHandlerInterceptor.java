package com.ming.web.Interceptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ming.web.constant.CommonConstant;
import com.ming.web.utils.TokenUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * token拦截器，这里不做具体拦截，将token存入ThreadLocal中
 */
public class TokenHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (StringUtils.hasLength(token)){
            TokenUtils.setPageNum(token);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TokenUtils.remove();
    }

}
