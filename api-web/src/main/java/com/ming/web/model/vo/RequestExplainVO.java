package com.ming.web.model.vo;

import lombok.Data;

/**
 * 请求参数说明
 */
@Data
public class RequestExplainVO {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 是否必须
     */
    private String must;

    /**
     * 参数说明
     */
    private String desc;

}
