package com.ming.web.model.vo;

import lombok.Data;

@Data
public class AddInterfaceCountVO {

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 要添加的次数
     */
    private String addCount;

    /**
     * 用户密码
     */
    private String userPassword;

}
