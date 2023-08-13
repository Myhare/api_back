package com.ming.web.model.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户访问来源
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitSourceDTO {

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 浏览量
     */
    private Integer viewsCount;


}
