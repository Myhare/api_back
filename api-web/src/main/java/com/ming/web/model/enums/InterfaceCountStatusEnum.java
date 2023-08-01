package com.ming.web.model.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 *
 * @author yupi
 */
@AllArgsConstructor
public enum InterfaceCountStatusEnum {

    ALL("all", "所有接口"),
    HAVE("have", "拥有接口"),
    NO_HAVE("noHave", "接口调用次数用尽");

    private final String text;

    private final String desc;

    public static InterfaceCountStatusEnum getEnumByText(String text){
        for (InterfaceCountStatusEnum value : InterfaceCountStatusEnum.values()) {
            if (value.text.equals(text)){
                return value;
            }
        }
        return null;
    }
}
