package com.ming.web.constant;

/**
 * 通用常量
 *
 * @author yupi
 */
public interface CommonConstant {

    /**
     * 系统管理员用户id
     */
    Integer ADMIN_ID = 1;

    /**
     * 盐值，混淆密码
     */
    String SALT = "ming";

    /**
     * 请求头token的key
     */
    String TOKEN_HEADER = "token";

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";
}
