package com.zero.webmagic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-20:20
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class SpiderConfig {

   @Bean
    public Spider spider(Pipeline pipeline,PageProcessor pageProcessor,Downloader downloader){
       return Spider.create(pageProcessor)
               .setDownloader(downloader)
               .addPipeline(pipeline)
               .thread(50);

   }
}
