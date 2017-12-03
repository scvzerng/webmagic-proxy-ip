package com.zero.webmagic.scan;

import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import com.zero.webmagic.entity.ScanIp;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-22:11
 * Project:webmagic-demo
 * Package:com.zero.webmagic.scan
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class IpConsumer extends Thread {
    private IpRepository ipRepository;

    public IpConsumer(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    @Override
    public void run() {
        while (true){
            ScanIp scanIp = null;
            try {
                    scanIp = IpGenerator.SCAN_IPS.take();

            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }

            try {
                if (scanIp == null) return;
                InetAddress inetAddress = InetAddress.getByName(scanIp.getIp());
                Socket socket = new Socket(inetAddress, scanIp.getPort());
                socket.setSoTimeout(200);
                socket.close();
                Ip ip = new Ip();
                ip.setIp(scanIp.getIp());
                ip.setPort(scanIp.getPort());
                if (ipRepository.findByIp(ip.getIp()) == null) {
                    ipRepository.save(ip);
                    log.error("扫描到=>{}:{}", scanIp.getIp(), scanIp.getPort());
                }
            } catch (IOException e) {
                log.error("扫描=>{}:{} 不存在", scanIp.getIp(), scanIp.getPort());


            }
        }

    }
}