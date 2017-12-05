package com.zero.webmagic.controller;

import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.dao.UrlRepository;
import com.zero.webmagic.entity.Ip;
import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.FetchStatusEnum;
import com.zero.webmagic.scan.IpConsumer;
import com.zero.webmagic.scan.IpGenerator;
import org.springframework.web.bind.annotation.*;
import us.codecraft.webmagic.Spider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    UrlRepository urlRepository;
    @Resource
    IpRepository ipRepository;

    private static final String DEFAULT_URL = "http://www.kuaidaili.com/free/inha/";
    @Resource
    Spider spider;
    @GetMapping
    public List<Ip> list(){
        Ip ip = new Ip();
        ip.setIp("127.0.0.1");
        ip.setPort(8080);
        return new ArrayList<>(Collections.singletonList(ip));
    }

    @PostMapping
   public Ip add(@RequestBody Ip ip){
        return ipRepository.save(ip);
    }
    @GetMapping("/fetch")
    public void fetch(@RequestParam(defaultValue = DEFAULT_URL) String url){
        Url root = urlRepository.findUrlByUrl(url);

        if(root!=null){
            //装载初始url
            Optional
                    .ofNullable(
                            urlRepository.findUrlsByParentIdAndStatusIn(root.getId(),
                                    FetchStatusEnum.FAIL,
                                    FetchStatusEnum.LOCK)
                    ).ifPresent(
                    list->{
                            urlRepository.saveAll(list.stream().peek(u-> u.setStatus(FetchStatusEnum.FAIL)).collect(Collectors.toList()));
                            list.stream()
                                    .map(Url::getUrl)
                                    .forEach(spider::addUrl);
                    });
            if(root.getStatus()==FetchStatusEnum.FAIL){
                spider.addUrl(url);
            }
        }else{
            spider.addUrl(url);

        }

        spider.setSpawnUrl(true).start();
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
