package com.ming.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderQueryInfoVO {

    /**
     * 查询当前的页数
     */
    private int current;

    /**
     * 查询的数量
     */
    private int pageSize;

    /**
     * 搜索内容
     */
    private String keyword;

    /**
     * 接口次数状态
     */
    private String orderStatus;

    /**
     * 获取分页的信息
     */
    public Integer getLimitCurrent(){
        return (current - 1) * pageSize;
    }
}
