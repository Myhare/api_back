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
    private Integer addCount;

}
