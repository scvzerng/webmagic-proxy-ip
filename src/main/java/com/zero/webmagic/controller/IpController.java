package com.zero.webmagic.controller;

import com.zero.webmagic.IpPageProcessor;
import com.zero.webmagic.IpPiple;
import com.zero.webmagic.ReplaceInvalidHttpDownloader;
import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import com.zero.webmagic.scan.IpConsumer;
import com.zero.webmagic.scan.IpGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-18:22
 * Project:webmagic-demo
 * Package:com.zero.webmagic.controller
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/ips")
public class IpController {

    @Resource
    IpRepository ipRepository;

    private static final String DEFAULT_URL = "http://www.xicidaili.com/nn/";
    @Resource
    Spider spider;
    @GetMapping
    public List<Ip> list(){
        Ip ip = new Ip();
        ip.setIp("127.0.0.1");
        ip.setPort(8080);
        return new ArrayList<>(Collections.singletonList(ip));
    }


    @GetMapping("/fetch")
    public void fetch(@RequestParam(defaultValue = DEFAULT_URL) String url){

        spider.addUrl(url).setSpawnUrl(true).start();
    }

    @GetMapping("/scan")
    public void scan(@RequestParam(defaultValue = "1") Integer threads){
        IpGenerator ipGenerator = new IpGenerator();
        ipGenerator.start();

        for(int i=0;i<threads;i++){
            IpConsumer ipConsumer = new IpConsumer(ipRepository);
            ipConsumer.start();
        }
    }

}
