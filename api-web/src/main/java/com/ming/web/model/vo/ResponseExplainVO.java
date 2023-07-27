package com.ming.web.model.vo;

import lombok.Data;

/**
 * 响应参数说明
 */
@Data
public class ResponseExplainVO {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数说明
     */
    private String desc;

}
