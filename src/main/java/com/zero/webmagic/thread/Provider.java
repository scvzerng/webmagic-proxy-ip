package com.zero.webmagic.thread;

import com.zero.webmagic.entity.Ip;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description
 * <p>
 * 2017-12-06 13:43
 *
 * @author scvzerng
 **/
@Slf4j
public class Provider extends Thread {
    private static  BlockingQueue<Integer> queue ;
    private static AtomicInteger integer = new AtomicInteger(0);
    public Provider(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true){
                Thread.sleep(1000);

                queue.put(integer.getAndIncrement());
//                log.info("{}",queue.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
