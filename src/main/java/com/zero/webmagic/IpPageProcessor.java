package com.zero.webmagic;

import com.zero.webmagic.dao.IpUrlRepository;
import com.zero.webmagic.dao.UrlRepository;
import com.zero.webmagic.entity.Ip;
import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.FetchStatusEnum;
import com.zero.webmagic.exception.ErrorPageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
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
@Slf4j
public class IpPageProcessor implements PageProcessor {
    @Resource
    private UrlRepository urlRepository;
    public void process(Page page) {

        List<Ip> pageIps = new ArrayList<>();

        page.getHtml()
                .$("#list")
                .xpath("//tbody/tr")
                .nodes()
                .forEach(node->{
                    List<Selectable> nodes = node.xpath("//tr/td").nodes();
                    if(nodes.size()>0){
                        Ip ip = new Ip();
                        ip.setIp(nodes.get(0).xpath("//td/text()").get());
                        ip.setPort(Integer.valueOf(nodes.get(1).xpath("//td/text()").get()));
                        ip.setIsOpen(nodes.get(2).xpath("//td/text()").get().equals("高匿名"));
                        ip.setType(nodes.get(3).xpath("//td/text()").get());
                        ip.setCity(nodes.get(4).xpath("//td/text()").get());
                        ip.setSpeed(nodes.get(5).xpath("//td/text()").get());
                        ip.setCanUse(false);
                        ip.setFailCount(0);
                        ip.setInsertTime(LocalDateTime.now());
                        ip.setCheckTime(LocalDateTime.parse(nodes.get(6).xpath("//td/text()").get(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        pageIps.add(ip);
                    }

                });
        System.out.println(String.format("url:%s data:%d",page.getUrl(),pageIps.size()));
        page.putField("ips",pageIps);
        Url url = urlRepository.findUrlByUrl(page.getUrl().get());

        url.setStatus(FetchStatusEnum.SUCCESS);
        urlRepository.save(url);
        List<String> links = page.getHtml().links().regex(".+/inha/\\d+").all();
        page.addTargetRequests(links);

    }

    public Site getSite() {
        return Site.me()
                .setCharset("UTF-8")
                .setSleepTime(3000)
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
    }
}
