package com.ming.web.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 令牌桶实现
 */
public class TokenBucket {

    // 令牌桶容量
    private final int capacity;
    // 每秒生成多少个令牌
    private final double refillRate;
    // 当前可用令牌数量
    private double tokensAvailable;
    // 上次补充令牌的时间戳
    private long lastRefillTimestamp;

    /**
     * 发送邮件令牌桶，每60秒只能发送一个邮件
     */
    public static Map<String, TokenBucket> emailTokenBuckets = new ConcurrentHashMap<>();

    /**
     * 对一个邮箱生成令牌桶
     * @param email 邮箱
     * @return      令牌桶
     */
    public static TokenBucket getEmailTokenBucket(String email) {
        return emailTokenBuckets.computeIfAbsent(email, key -> new TokenBucket(1, (double) 1 /60));
    }

    private TokenBucket(int capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        // 初始化可用令牌数量为最大容量
        this.tokensAvailable = capacity;
        // 初始化上次补充时间为当前时间
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    // 令牌是否足够
    public boolean tryConsume() {
        refillTokens(); // 先补充令牌
        if (tokensAvailable >= 1) {
            tokensAvailable--; // 消耗一个令牌
            return true; // 返回成功
        }
        return false; // 令牌不足，消耗失败
    }

    // 补充令牌
    // 补充令牌
    private void refillTokens() {
        // 当前时间
        long currentTime = System.currentTimeMillis();
        // 计算距离上次增加令牌的时间
        double elapsedTimeSeconds = (currentTime - lastRefillTimestamp) / 1000.0;
        // 计算需要补充的令牌数量
        double tokensToAdd = elapsedTimeSeconds * refillRate;
        // 更新令牌数量，不超过容量
        tokensAvailable = Math.min(capacity, tokensAvailable + tokensToAdd);
        lastRefillTimestamp = currentTime; // 更新上次补充时间
    }

    // TODO 定时任务，定时清除不需要的令牌桶

}
