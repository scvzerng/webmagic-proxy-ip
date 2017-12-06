package com.zero.webmagic.proxy;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 阻塞线程池
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/6-22:36
 * Project:webmagic-demo
 * Package:com.zero.webmagic.proxy
 * To change this template use File | Settings | File Templates.
 */

public class FixedBlockThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    //阻塞队列
    private final BlockingQueue<Runnable> blockingQueue  ;

    private FixedBlockThreadPoolTaskExecutor(int queueSize, int poolSize) {
        this.blockingQueue = new LinkedBlockingDeque<>(queueSize);
        this.setMaxPoolSize(poolSize);
        this.setCorePoolSize(poolSize);
    }

    @Override
    public void execute(Runnable task) {
        try {
            //使用阻塞队列put阻塞的特性使队列满时无法放入task
            //当一个任务被线程池取出后队列又使其解除阻塞
            //主要用于线程池满负荷运作时防止其余线程无限制得给队列添加元素
            blockingQueue.put(task);
            super.execute(blockingQueue.take());
        } catch (InterruptedException ignore) {
         Thread.currentThread().interrupt();
        }
    }

    @Override
    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        return blockingQueue;
    }


    public static ThreadPoolTaskExecutor newFixedThreadPool(int queueSize, int poolSize){
        FixedBlockThreadPoolTaskExecutor taskExecutor = new FixedBlockThreadPoolTaskExecutor(queueSize,poolSize);
        taskExecutor.initialize();
        return taskExecutor;
    }

}
