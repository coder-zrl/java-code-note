package com.bird.limiter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author coder-zrl@qq.com
 * 2025-04-26
 * 滑动窗口限流器
 * 原理：也是计数器算法，但是精度更精细，将时间范围拆分为多个时间间隔，每个时间间隔内都进行计数，sentinel也是使用的这个算法
 * 缺点：目前有两个问题：高并发场景下槽扩大会和limit更新槽计数有并发冲突
 */
public class RollingWindowLimiter implements RateLimiter {
    private AtomicInteger curCount = new AtomicInteger(); // 当前大时间窗口内的请求数量

    private LinkedList<Integer> slots = new LinkedList<>(); // 时间窗口计数器，用来记录每个窗口的请求数量
    private int maxLimitNum = 1000; // 每秒限流阈值
    private long windowsLength = 100L; // 每个时间窗口大小，此处是100ms
    private int windowsNum = 10; // 时间窗口数量，此处是10个时间窗口

    public RollingWindowLimiter() {
        new Thread(() -> {
            while (true) {
                // 每隔100ms，就添加一个时间窗口
                try {
                    Thread.sleep(windowsLength);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slots.addLast(0);
                // 如果队列长度大于10，则将队列中第一个窗口的请求数量从当前大时间窗口内请求数量中减去，并移除队列中的第一个窗口
                if (slots.size() > windowsNum) {
                    curCount.addAndGet(-slots.peekFirst());
                    slots.removeFirst();
                }
            }
        }).start();
    }

    @Override
    public boolean limit() {
        if (curCount.get() > maxLimitNum) {
            return true;
        }
        slots.set(slots.size() - 1, slots.peekLast() + 1);
        curCount.incrementAndGet();
        return false;
    }
}
