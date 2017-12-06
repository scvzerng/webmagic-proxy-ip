package com.zero.webmagic.proxy;

import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-13:56
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
@Slf4j
public class JDBCProxyProvider implements ProxyProvider {
    @Resource
    IpRepository ipRepository;
    //有效IP队列
    private static  BlockingQueue<Ip> validIp = new LinkedBlockingQueue<>(10);
    //锁定队列
    private static  Map<Ip,Ip> lockIp = new ConcurrentHashMap<>();
    //失效队列
    private static  BlockingQueue<Ip> invalidIp = new LinkedBlockingQueue<>(10);

    private static  ExecutorService executorService = Executors.newFixedThreadPool(30);

    private static  AtomicBoolean INIT = new AtomicBoolean(true);
    private static  AtomicInteger pageNum = new AtomicInteger(0);
    private static  AtomicInteger total = new AtomicInteger(0);

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
            if ((Objects.isNull(page) || !page.isDownloadSuccess()) && Objects.nonNull(proxy)) {
                if(proxy instanceof IpProxy){
                    IpProxy ipProxy = (IpProxy) proxy;
                    this.makeIpInvalid(ipProxy.getIp());
                }


            }
    }

    @Override
    public Proxy getProxy(Task task) {
            if (INIT.getAndSet(false)) return null;

        try {
            return new IpProxy(this.getValidIp());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void addValidIp(Ip ip) throws InterruptedException {
        if(ip==null) return;
        if(validIp.contains(ip)) return;
        validIp.put(ip);
    }

    public Ip getValidIp() throws InterruptedException {
        Ip ip = validIp.take();
        lockIp.put(ip,ip);
        return ip;
    }

    public void makeIpInvalid(Ip ip) {
        Ip locked =  lockIp.remove(ip);
        if(locked==null) return;
        try {
            ip.setCanUse(false);
            ip.setFailCount(ip.getFailCount() == null ? 0 : ip.getFailCount() + 1);
            invalidIp.put(ip);
            log.info("{}:{} [{}] is invalid",ip.getIp(),ip.getPort(),ip.getFailCount());
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
        }
    }

    public void addInValidIp(Ip ip) {
        if(invalidIp.contains(ip)) return;
        try {
            invalidIp.put(ip);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Ip getInvalidIp() throws InterruptedException {
        return invalidIp.take();
    }

    private void checkIp(Ip ip){
        ip.setUpdateTime(LocalDateTime.now());

        try {
            InetAddress address = InetAddress.getByName(ip.getIp());
            Socket socket = new Socket(address,ip.getPort());
            socket.close();
            ip.setCanUse(true);
            ipRepository.save(ip);
            this.addValidIp(ip);
            log.info("is valid {}:{}",ip.getIp(),ip.getPort());
        } catch (IOException e) {
            ipRepository.delete(ip);
            log.info("delete {}:{}",ip.getIp(),ip.getPort());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * 每秒抽取100IP放到队列等待验证其有效性
     */
    public void fetchValidIps(){
        while (true){
            Pageable pageable = PageRequest.of(pageNum.getAndIncrement(),100, Sort.by(Sort.Order.asc("updateTime")));
            org.springframework.data.domain.Page<Ip> page = ipRepository.findAll(pageable);
            total.getAndSet(page.getTotalPages());

            if(page.hasContent()){
                page.getContent().forEach(this::addInValidIp);

            }else{
                log.info("total:{} current:{} reset 0",total.get(),pageNum.get());
                pageNum.set(0);
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @PostConstruct
    public void init(){
        new Thread(this::fetchValidIps).start();
        new Thread(()->{

                while (true) {
                    try {
                        Ip  ip = this.getInvalidIp();
                        executorService.submit(() -> checkIp(ip));
                        if (ip == null) break;

                    } catch (InterruptedException ignore) {
                    }

                }

        }).start();
    }


    /**
     * 每30秒钟发起对Ip的有效性验证
     */

    @PreDestroy
    public void destroy(){
        while(true){
            Ip ip =  invalidIp.poll();
            if(ip==null) break;
            ipRepository.save(ip);

        }
    }
}
