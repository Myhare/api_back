package com.ming.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ming.web.model.dto.InvokeCountDTO;
import com.ming.web.model.dto.statistics.DataInvokeStatisticsDTO;
import com.ming.web.model.entity.InvokeCount;

import java.util.List;

/**
* @author 86135
* @description 针对表【invoke_count】的数据库操作Service
* @createDate 2023-08-02 10:58:15
*/
public interface InvokeCountService extends IService<InvokeCount> {

    /**
     * 查看最近七天的接口调用情况
     * @return
     */
    List<DataInvokeStatisticsDTO> listInvokeCountDTO();

}
