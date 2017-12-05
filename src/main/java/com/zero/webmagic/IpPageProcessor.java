package com.zero.webmagic;

import com.zero.webmagic.entity.Ip;
import com.zero.webmagic.exception.ErrorPageException;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-12:53
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
public class IpPageProcessor implements PageProcessor {
    Map<String,Boolean> isProcessing = new ConcurrentHashMap<>();
    public void process(Page page) {
        if(isProcessing.getOrDefault(page.getUrl().get(),false)) return;
        List<Ip> pageIps = new ArrayList<>();
        isProcessing.put(page.getUrl().get(),true);
        page.getHtml()
                .$("#ip_list")
                .xpath("//tbody/tr")
                .nodes()
                .forEach(node->{
                    List<Selectable> nodes = node.xpath("//tr/td").nodes();
                    if(nodes.size()>0){
                        Ip ip = new Ip();
                        ip.setIp(nodes.get(1).xpath("//td/text()").get());
                        ip.setPort(Integer.valueOf(nodes.get(2).xpath("//td/text()").get()));
                        ip.setCity(nodes.get(3).xpath("//a/text()").get());
                        ip.setIsOpen(nodes.get(4).xpath("//td/text()").get().equals("高匿"));
                        ip.setType(nodes.get(5).xpath("//td/text()").get());
                        ip.setSpeed(nodes.get(6).xpath("//td/div/@title").get());
                        ip.setConnectTime(nodes.get(7).xpath("//td/div/@title").get());
                        ip.setAliveTime(nodes.get(8).xpath("//td/text()").get());
                        ip.setCanUse(false);
                        ip.setFailCount(0);
                        ip.setInsertTime(LocalDateTime.now());
                        ip.setCheckTime(LocalDateTime.parse(nodes.get(9).xpath("//td/text()").get(),DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")));
                        pageIps.add(ip);
                    }

                });
        System.out.println(String.format("url:%s data:%d",page.getUrl(),pageIps.size()));
        page.putField("ips",pageIps);
        List<String> links = page.getHtml().links().regex(".+/nn/\\d+").all();
        List<String> unProcessed = links.stream().filter(link->!isProcessing.containsKey(link)).collect(toList());
        unProcessed.forEach(link-> isProcessing.put(link,false));

        page.addTargetRequests(unProcessed);

    }

    public Site getSite() {
        return Site.me()
                .setCharset("UTF-8")
                .setSleepTime(3000)
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
    }
}
