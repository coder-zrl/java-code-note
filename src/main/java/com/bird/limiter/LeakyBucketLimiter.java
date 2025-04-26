package com.bird.limiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author coder-zrl@qq.com
 * 2025-04-26
 * 漏斗算法
 * 原理：以恒定速率处理请求，超出桶容量则拒绝请求。
 */
public class LeakyBucketLimiter implements RateLimiter {
    private long capacity; // 桶容量，即QPS限流阈值
    private long leakRate; // 漏水速率（请求/毫秒）
    private long waterLevel = 0; // 当前桶中剩余的水
    private volatile long lastLeakTime; // 上次漏水时间


    @Override
    public boolean limit() {
        long now = System.currentTimeMillis();
        // 先漏水，计算剩余水量
        waterLevel = Math.max(0, waterLevel - ((now - lastLeakTime) / 1000) * leakRate);
        // 容量满了，被限流
        if (waterLevel + 1 > capacity) {
            return true;
        }
        waterLevel++;
        return false;
    }
}
