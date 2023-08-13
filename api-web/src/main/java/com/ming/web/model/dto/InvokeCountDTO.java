package com.ming.web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 访问量
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvokeCountDTO {

    /**
     * 日期
     */
    private String day;

    /**
     * 访问量
     */
    private Integer viewsCount;

}
