package com.fengxuechao.jvm.threadpool;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * 如何合理设置线程池的大小
 */
public class MyPoolSizeCalculator extends PoolSizeCalculator {

    public static void main(String[] args) {
        MyPoolSizeCalculator calculator = new MyPoolSizeCalculator();
        calculator.calculateBoundaries(
                // CPU目标利用率
                new BigDecimal(1.0),
                // blockingqueue占用的内存大小，byte
                new BigDecimal(100000));

        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        8,
                        8,
                        // 默认情况下指的是非核心线程的空闲时间
                        // 如果allowCoreThreadTimeOut=true：核心线程/非核心线程允许的空闲时间
                        10L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(2500),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                );
    }

    @Override
    protected long getCurrentThreadCPUTime() {
        // 当前线程占用的总时间
        return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    @Override
    protected Runnable creatTask() {
        return new AsynchronousTask();
    }

    @Override
    protected BlockingQueue createWorkQueue() {
        return new LinkedBlockingQueue<>();
    }

}

class AsynchronousTask implements Runnable {

    @Override
    public void run() {
//         System.out.println(Thread.currentThread().getName());
    }
}