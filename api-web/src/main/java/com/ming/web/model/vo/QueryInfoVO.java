package com.ming.web.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryInfoVO {

    /**
     * 查询当前的页数
     */
    @ApiModelProperty(name = "keywords", value = "查询页数", dataType = "String")
    private int current;

    /**
     * 查询的数量
     */
    @ApiModelProperty(name = "keywords", value = "属性个数", dataType = "String")
    private int pageSize;

    /**
     * 搜索内容
     */
    @ApiModelProperty(name = "keywords", value = "搜索内容", dataType = "String")
    private String keyword;

    /**
     * 获取分页的信息
     */
    public Integer getLimitCurrent(){
        return (current - 1) * pageSize;
    }
}
