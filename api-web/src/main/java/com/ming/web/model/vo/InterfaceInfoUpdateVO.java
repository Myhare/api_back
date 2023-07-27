package com.ming.web.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 接口信息封装视图
 *
 */
@Data
public class InterfaceInfoUpdateVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 服务器地址
     */
    private String host;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     * [
     *   {"name": "username", "type": "string"}
     * ]
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 请求参数说明
     */
    private List<RequestExplainVO> requestExplain;

    /**
     * 响应参数说明
     */
    private List<ResponseExplainVO> responseExplain;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    private static final long serialVersionUID = 1L;
}
