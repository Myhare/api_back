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
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 当前页码
     */
    public static final String CURRENT = "current";

    /**
     * 页码条数
     */
    public static final String SIZE = "pageSize";

    /**
     * 默认条数
     */
    public static final String DEFAULT_SIZE = "10";

    /**
     * 无限次数
     */
    public static final String INFINITE = "无限制";

    /**
     * 为0的浮点
     */
    public static final Double DOUBLE_NULL = 0.0;

    /**
     * 为空的整数
     */
    public static final Double INTEGER_NULL = 0.0;
}
