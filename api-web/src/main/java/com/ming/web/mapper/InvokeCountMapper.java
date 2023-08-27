package com.ming.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.web.model.dto.InvokeCountDTO;
import com.ming.web.model.dto.statistics.DataInvokeStatisticsDTO;
import com.ming.web.model.entity.InvokeCount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author 86135
* @description 针对表【invoke_count】的数据库操作Mapper
* @createDate 2023-08-02 10:58:15
* @Entity generator.domain.InvokeCount
*/
public interface InvokeCountMapper extends BaseMapper<InvokeCount> {

    /**
     * 获取最近七天的接口调用情况
     * @return  查询结果
     */
    List<DataInvokeStatisticsDTO> getInvokeCountDTO(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}




