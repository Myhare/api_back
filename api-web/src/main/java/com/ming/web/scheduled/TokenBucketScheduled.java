package com.ming.web.scheduled;

import com.ming.apiCommon.dubbo.RedisService;
import com.ming.web.utils.TokenBucket;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static com.ming.apiCommon.constant.RedisPrefixConst.API_REGISTER_CODE;

/**
 * 令牌桶定时清除
 */
@Component
public class TokenBucketScheduled {

    @Resource
    private RedisService redisService;

    /**
     * 每天执行一次，将令牌桶中不需要的值清除
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearEmailBucket(){
        Map<String, TokenBucket> emailTokenBuckets = TokenBucket.emailTokenBuckets;

        // 使用迭代器删除，防止报错
        for (Iterator<Map.Entry<String, TokenBucket>> iterator = emailTokenBuckets.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<String, TokenBucket> tokenBucketEntry = iterator.next();
            String email = tokenBucketEntry.getKey();
            String redisKey = API_REGISTER_CODE + email;
            if (redisService.get(redisKey) == null){
                // 说明当前邮箱注册已经完成，清除对应邮箱的令牌桶
                iterator.remove();
            }
        }
    }

}
