package com.bird.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author coder-zrl@qq.com
 * @date 2024-11-01
 */
public class AsyncThreadExample {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("执行step 1");
            return "step1 result";
        }, executor);

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("执行step 2");
            return "step2 result";
        });

        cf1.thenCombine(cf2, (result1, result2) -> {
            System.out.println(result1 + " , " + result2);
            System.out.println("执行step 3");
            return "step3 result";
        }).thenAccept(result3 -> System.out.println(result3));


        // 如果不指定线程池，则会使用默认线程池
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000); // 模拟任务处理时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Task " + taskId + " completed";
            }, executor);
            futures.add(future);
        }
        // 等待所有任务完成并处理结果
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRun(() -> {
            List<String> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            results.forEach(System.out::println);
        });
        // 阻塞等待所有任务完成
        allFutures.join();

        // 关闭 ExecutorService
        executor.shutdown();
    }
}
