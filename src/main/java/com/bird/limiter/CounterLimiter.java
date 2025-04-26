package com.bird.limiter;

/**
 * @author coder-zrl@qq.com
 * 2025-04-26
 * 计数器限流器
 * 原理：单位时间内的请求数量，超过阈值则限流，否则不限流
 * 优点：实现比较简单
 * 缺点：在两个时间间隔缝隙中，可能会导致流量超过QPS阈值
 */
public class CounterLimiter implements RateLimiter {
    private int maxLimitNum = 1000; // 每秒限流阈值
    private int curCount = 0; // 当前时间段内请求数量
    private long startTime = System.currentTimeMillis(); // 当前时间段起始时间，毫秒时间戳
    private long interval = 1000; // 时间间隔，毫秒单位

    /**
     * 这里需要加锁，防止多线程并发问题
     *
     * @return true 表示限流，false 表示不限流
     */
    // PS：可以使用Redis的incr命令实现计数器限流，对当前时间取整作为key，
    // 如果incr结果超过阈值则需要限流，同时可以使用Redis的key过期淘汰策略。
    @Override
    public synchronized boolean limit() {
        if (System.currentTimeMillis() - startTime > interval) {
            // 超过时间间隔，重置
            startTime = System.currentTimeMillis();
            curCount = 0;
        }
        if (curCount < maxLimitNum) {
            curCount++;
            return false;
        }
        return true;
    }
}
