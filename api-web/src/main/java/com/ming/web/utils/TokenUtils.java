package com.ming.web.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class TokenUtils {

    public static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    /**
     * 设置token信息
     * @param token token信息
     */
    public static void setPageNum(String token){
        TOKEN_HOLDER.set(token);
    }

    /**
     * 获取当前登录用户的token
     */
    public static String getToken(){
        return TOKEN_HOLDER.get();
    }

    /**
     * 删除token
     */
    public static void remove() {
        TOKEN_HOLDER.remove();
    }
}
