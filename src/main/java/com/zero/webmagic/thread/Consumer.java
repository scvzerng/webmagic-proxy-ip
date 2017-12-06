package com.zero.webmagic.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * description
 * <p>
 * 2017-12-06 13:43
 *
 * @author scvzerng
 **/
@Slf4j
public class Consumer extends Thread {
    private static  BlockingQueue<Integer> queue ;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true){
                log.info("{} - {}",queue.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
