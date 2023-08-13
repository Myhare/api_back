package com.ming.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderAddVO {


    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 单价
     */
    private Double charging;

    /**
     * 购买数量
     */
    private Integer count;

    /**
     * 订单应付价格
     */
    private BigDecimal totalAmount;



}
