package com.ming.apiCommon.constant;

/**
 * redis前缀点赞
 * @author liuziming
 * @date 2022/10/8
 */
public interface RedisPrefixConst {

    /**
     * 登录用户id
     */
    String LOGIN_USER_ID = "login_user_id:";

    /**
     * 验证码
     * 前缀:用户邮箱
     */
    String API_REGISTER_CODE = "api_register_code:";

    /**
     * 接口最近7天调用次数
     */
    String INVOKE_DATE_COUNT = "invoke_data_count";
}
