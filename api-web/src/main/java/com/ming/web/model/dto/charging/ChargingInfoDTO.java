package com.ming.web.model.dto.charging;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 返回给后端接口计费信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingInfoDTO {

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口计费
     */
    private Double charging;

    /**
     * 接口状态
     */
    private Integer status;

    /**
     * 剩余可调用次数
     */
    private String availablePieces;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
