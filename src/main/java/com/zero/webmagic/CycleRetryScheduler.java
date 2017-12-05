package com.zero.webmagic;

import com.zero.webmagic.controller.IpController;
import com.zero.webmagic.dao.UrlRepository;
import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.FetchStatusEnum;
import javafx.scene.Parent;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-22:18
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CycleRetryScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {
    @Resource
    private UrlRepository urlRepository;
    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();
    AtomicLong parentId = new AtomicLong(0);
    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        Url url = urlRepository.findUrlByUrl(request.getUrl());
        LocalDateTime now = LocalDateTime.now();

        if(url==null){
            url = new Url();
            url.setStatus(FetchStatusEnum.LOCK);
            url.setUrl(request.getUrl());
            url.setInsertTime(now);
            url.setUpdateTime(now);
            if(parentId.get()>0L){
                Url parent = new Url();
                parent.setId(parentId.get());
                url.setParent(parent);

            }
            urlRepository.save(url);
            if(parentId.get()==0){
            parentId.set(url.getId());

            }

        }else{
            if(url.getStatus()==FetchStatusEnum.SUCCESS){
                return;
            }
            url.setStatus(FetchStatusEnum.LOCK);
            url.setUpdateTime(now);
            urlRepository.save(url);

        }

        queue.add(request);

    }

    @Override
    public void push(Request request, Task task) {
        pushWhenNoDuplicate(request,task);
    }

    @Override
    public Request poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
