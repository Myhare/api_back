package com.ming.web.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 返回给前端的用户信息和权限
 */
@Data
@Builder
public class LoginUserInfoVO {

    /**
     * 角色基本信息
     */
    private UserVO user;

    /**
     * JWT生成的token
     */
    private String token;

    /**
     * 角色列表
     */
    private List<String> roleList;

}
