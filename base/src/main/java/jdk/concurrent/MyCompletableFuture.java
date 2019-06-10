package jdk.concurrent;

/**
 * @author chenxiang
 * 2018/12/11
 */
public class MyCompletableFuture {
    // CompletableFuture vs ParallelStream
    // 前者更灵活，计算密集型人物可以直接用并行流（jvm所有并行流使用单例的ForkJoinPool线程池，共享一个线程数参数），IO等待型任务建议使用前者，可以自定义线程池（调大线程数）
}
