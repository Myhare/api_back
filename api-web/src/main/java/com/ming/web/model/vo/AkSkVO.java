package com.ming.web.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AkSkVO {

    /**
     * 签名 accessKey
     */
    private String accessKey;

    /**
     * 签名 secretKey
     */
    private String secretKey;

}
