package com.ming.web.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

@Data
public class UserInterfaceVO {

    /**
     * 接口id
     */
    private Long interfaceInfoId;

    /**
     * 名称
     */
    private String name;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;
}
