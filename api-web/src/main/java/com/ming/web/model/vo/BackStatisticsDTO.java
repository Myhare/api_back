package com.ming.web.model.vo;

import com.ming.web.model.dto.statistics.DataInvokeStatisticsDTO;
import com.ming.web.model.dto.statistics.InterfaceInvokeStatisticsDTO;
import com.ming.web.model.dto.statistics.UserInvokeDTO;
import com.ming.web.model.dto.statistics.VisitSourceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 后台统计信息
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackStatisticsDTO {

    /**
     * 用户访问来源
     */
    private List<VisitSourceDTO> articleRankDTOList;

    /**
     * 一周接口调用次数
     */
    private List<DataInvokeStatisticsDTO> invokeCountStatisticsList;

    /**
     * 接口调用总数量统计
     */
    private List<InterfaceInvokeStatisticsDTO> interfaceInvokeStatisticsList;

    /**
     * 用户调用接口排行
     */
    private List<UserInvokeDTO> userInvokeList;
}
