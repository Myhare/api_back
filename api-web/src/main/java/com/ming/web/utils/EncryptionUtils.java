package com.ming.web.utils;

import org.springframework.util.DigestUtils;

import static com.ming.web.constant.CommonConstant.SALT;

/**
 * 加密工具类
 */
public class EncryptionUtils {

    /**
     * md5盐值加密
     * @param s 原字符串
     * @return 加密后字符串
     */
    public static String md5SaleEncryptString(String s){
        return DigestUtils.md5DigestAsHex((SALT + s).getBytes());
    }

}
