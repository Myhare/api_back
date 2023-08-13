package com.ming.web.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeChargingVO {

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 要修改的接口定价
     */
    private Double changeCharging;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 接口剩余可调用次数
     */
    private String availablePieces;
}
