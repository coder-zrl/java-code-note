package com.bird.limiter;

/**
 * @author coder-zrl@qq.com
 * 2025-04-26
 * 令牌桶限流器
 * 原理：按固定速率生成令牌，请求需获取令牌才能处理。
 */
public class TokenBucketLimiter implements RateLimiter {
    private long lastTime; // 上次获取令牌的时间
    private long capacity; // 桶的容量
    private long tokenAddRate; // 令牌放入速率
    private long curTokenNum; // 当前令牌数量

    @Override
    public boolean limit() {
        long now = System.currentTimeMillis();
        // 先添加令牌
        curTokenNum = Math.min(capacity, curTokenNum + (now - lastTime) * tokenAddRate);
        lastTime = now;
        if (curTokenNum < 1) {
            return true;
        }
        curTokenNum -= 1;
        return false;
    }
}
