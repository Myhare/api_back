package com.ming.order.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    UN_PAY(0, "未支付"),
    PAYED(1, "已支付"),
    TIMEOUT(2, "已超时");

    private final int value;

    private final String desc;

}
