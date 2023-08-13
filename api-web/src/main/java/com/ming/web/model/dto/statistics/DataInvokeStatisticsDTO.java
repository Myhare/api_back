package com.ming.web.model.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章统计
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataInvokeStatisticsDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Integer count;

}
