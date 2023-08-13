package com.ming.web.model.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户调用接口排行
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInvokeDTO {

    /**
     * 标题
     */
    private String userName;

    /**
     * 浏览量
     */
    private Integer totalNum;


}
