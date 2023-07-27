package com.ming.web.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用来接收前端传递post字符串
 */
@Data
public class PasswordRequest implements Serializable {

    private String password;

}
