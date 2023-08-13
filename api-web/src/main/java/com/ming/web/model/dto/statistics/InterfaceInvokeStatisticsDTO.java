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
public class InterfaceInvokeStatisticsDTO {

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 数量
     */
    private Integer total;

}
