package com.zero.webmagic.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * description
 * <p>
 * 2017-12-06 13:46
 *
 * @author scvzerng
 **/
public class TestThreads {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        for(int i=0;i<2;i++){
            new Consumer(queue).start();

        }
        for(int i=0;i<10;i++){
            new Provider(queue).start();

        }

    }
}
