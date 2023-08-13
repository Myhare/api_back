package com.ming.apiCommon.utils;

/**
 * 公共校验
 */
public class CommonUtil {

    // 判断字符串是不是数字
    public static boolean isInteger(String s){
        return s.matches("[0-9]+");
    }
}
