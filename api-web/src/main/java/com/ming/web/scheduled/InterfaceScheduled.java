package com.ming.web.scheduled;

import cn.hutool.core.date.DateUtil;
import com.ming.apiCommon.constant.RedisPrefixConst;
import com.ming.apiCommon.dubbo.RedisService;
import com.ming.web.mapper.InvokeCountMapper;
import com.ming.web.model.entity.InvokeCount;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 接口相关定时任务
 */
@Component
public class InterfaceScheduled {

    @Resource
    private RedisService redisService;

    @Resource
    private InvokeCountMapper invokeCountMapper;

    /**
     * 每天获取一次接口调用次数存到数据库中
     * 每天执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveStatisticsInvokeCount(){
        Object invokeCountObject = redisService.get(RedisPrefixConst.INVOKE_DATE_COUNT);
        String invokeCountStr = Optional.ofNullable(invokeCountObject).orElse(0).toString();

        // 将当天的接口调用次数存到数据库中
        InvokeCount invokeCount = InvokeCount.builder()
                .createTime(DateUtil.yesterday())
                .invokeCount(invokeCountStr)
                .build();
        invokeCountMapper.insert(invokeCount);
    }

}
