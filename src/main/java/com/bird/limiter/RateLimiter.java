package com.bird.limiter;

/**
 * @author coder-zrl@qq.com
 * 2025-04-26
 */
public interface RateLimiter {
    /**
     * 执行限流方法
     *
     * @return true 表示限流成功，false 表示限流失败
     */
    boolean limit();

}
