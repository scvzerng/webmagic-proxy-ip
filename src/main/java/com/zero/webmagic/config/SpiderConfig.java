package com.zero.webmagic.config;

import com.zero.webmagic.downloader.ReplaceInvalidHttpDownloader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

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
    public Spider spider(Pipeline pipeline, PageProcessor pageProcessor, ReplaceInvalidHttpDownloader downloader, Scheduler scheduler){

      Spider spider = Spider.create(pageProcessor)
               .setDownloader(downloader)
               .addPipeline(pipeline)
               .setScheduler(scheduler)
               .thread(3);

       downloader.setSpider(spider);
       return spider;

   }
}
