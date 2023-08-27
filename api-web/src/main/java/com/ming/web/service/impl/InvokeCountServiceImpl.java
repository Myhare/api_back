package com.ming.web.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ming.apiCommon.constant.RedisPrefixConst;
import com.ming.apiCommon.dubbo.RedisService;
import com.ming.web.model.dto.statistics.DataInvokeStatisticsDTO;
import com.ming.web.model.entity.InvokeCount;
import com.ming.web.service.InvokeCountService;
import com.ming.web.mapper.InvokeCountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 调用统计服务
* @author 86135
* @description 针对表【invoke_count】的数据库操作Service实现
* @createDate 2023-08-02 10:58:15
*/
@Service
public class InvokeCountServiceImpl extends ServiceImpl<InvokeCountMapper, InvokeCount>
    implements InvokeCountService{

    @Resource
    private InvokeCountMapper invokeCountMapper;

    @Resource
    private RedisService redisService;

    // 查看最近7天的接口调用情况
    @Override
    public List<DataInvokeStatisticsDTO> listInvokeCountDTO() {
        DateTime startDate = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -6));
        DateTime endTime = DateUtil.endOfDay(new Date());
        List<DataInvokeStatisticsDTO> dataInvokeList = invokeCountMapper.getInvokeCountDTO(startDate, endTime);
        // 获取今天的访问量
        Object o = redisService.get(RedisPrefixConst.INVOKE_DATE_COUNT);
        String todayCount = Optional.ofNullable(o).orElse(0).toString();
        DataInvokeStatisticsDTO dataInvokeStatisticsDTO =
                new DataInvokeStatisticsDTO(DateUtil.today(), Integer.parseInt(todayCount));
        dataInvokeList.add(dataInvokeStatisticsDTO);
        return dataInvokeList;
    }
}




