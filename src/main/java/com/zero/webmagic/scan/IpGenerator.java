package com.zero.webmagic.scan;

import com.zero.webmagic.entity.ScanIp;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-22:02
 * Project:webmagic-demo
 * Package:com.zero.webmagic.scan
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class IpGenerator extends Thread {
    private int maxIp = 256;
    private int maxPort = 65536;
    int[] ports = new int[]{80,8080,3128,8081,9080,21,443,22,25,7001};
    public static volatile ArrayBlockingQueue<ScanIp> SCAN_IPS = new ArrayBlockingQueue<>(1000);
    @Override
    public void run(){
        for(int first=1;first<maxIp;first++){
            for(int second=1;second<maxIp;second++){
                for(int three=1;three<maxIp;three++){
                    for(int four=1;four<maxIp;four++){
                           try {
                               for(int port : ports){
                                   SCAN_IPS.put(new ScanIp(first,second,three,four,port));
                               }
                           } catch (InterruptedException e) {
                              log.error(e.getMessage());
                           }
                    }
                }
            }
        }
    }

}
