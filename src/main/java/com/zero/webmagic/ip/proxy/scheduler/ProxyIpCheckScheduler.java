package com.zero.webmagic.ip.proxy.scheduler;

import com.zero.webmagic.proxy.JDBCProxyProvider;
import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 代理IP状态检查 剔除连接不上的IP
 * <p>
 * 2017-12-05 17:00
 *
 * @author scvzerng
 **/
@Component
@Slf4j
public class ProxyIpCheckScheduler {
    private static final BlockingQueue<Ip> VALID_IP_POOLS = new LinkedBlockingQueue<>(10000);
    @Resource
    IpRepository ipRepository;

    @Resource
    private JDBCProxyProvider provider;
    private ExecutorService executorService = Executors.newFixedThreadPool(30);

    /**
     * 每秒钟对99个IP进行验证
     */
    @Scheduled(cron = "0/1 * * * * ?")
    @Async
    public void checkIpIsValid() throws InterruptedException {
        for(int i=0;i<99;i++){
            Ip ip = VALID_IP_POOLS.take();
            executorService.submit(()-> checkIp(ip));
        }
    }

    /**
     * 每30秒抽取100IP放到队列等待验证其有效性
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void fetchValidIps(){
        Pageable pageable = PageRequest.of(0,100, Sort.by(Sort.Order.asc("updateTime")));

        Page<Ip> page = ipRepository.findAll(pageable);
        if(page.hasContent()){
            page.getContent().forEach(ip->{
                try {
                    if(VALID_IP_POOLS.contains(ip)) return;
                    VALID_IP_POOLS.put(ip);
                } catch (InterruptedException ignore) {

                }

            });

        }
    }

    private void checkIp(Ip ip){
        ip.setUpdateTime(LocalDateTime.now());

        try {
            InetAddress address = InetAddress.getByName(ip.getIp());
            Socket socket = new Socket(address,ip.getPort());
            socket.close();
            ip.setCanUse(true);
            ipRepository.save(ip);
            log.info("is valid {}:{}",ip.getIp(),ip.getPort());
            synchronized (provider){
                provider.notifyAll();
            }
        } catch (IOException e) {
            ipRepository.delete(ip);
            log.info("delete {}:{}",ip.getIp(),ip.getPort());
        }
    }
}
