package com.zero.webmagic.core;

import com.zero.webmagic.dao.UrlRepository;
import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 对URL进行持久化和初始状态解锁
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-22:18
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
@Slf4j
public class CycleRetryScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();
    AtomicLong parentId = new AtomicLong(0);
    @Resource
    UrlRepository urlRepository;

    /**
     * 初始化解锁所有锁定的URL
     */
    @PostConstruct
    public void init() {
        List<Url> lockUrl = urlRepository.findUrlsByStatusIn(Status.LOCK);
        urlRepository.saveAll(lockUrl
                .stream()
                .parallel()
                .peek(url -> {
                    log.info("{} status from {} to {}", url.getUrl(), url.getStatus(), Status.FAIL);
                    url.setStatus(Status.FAIL);
                })
                .collect(Collectors.toList()));
    }

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        synchronized (urlRepository) {
            Url url = urlRepository.findUrlByUrl(request.getUrl());
            LocalDateTime now = LocalDateTime.now();

            if (url == null) {
                url = new Url();
                url.setStatus(Status.LOCK);
                url.setUrl(request.getUrl());
                url.setInsertTime(now);
                url.setUpdateTime(now);
                if (parentId.get() > 0L) {
                    Url parent = new Url();
                    parent.setId(parentId.get());
                    url.setParent(parent);

                }
                urlRepository.save(url);
                if (parentId.get() == 0) {
                    parentId.set(url.getId());

                }

            } else {
                if (url.getParent() != null) {
                    parentId.set(url.getParent().getId());
                }

                if (url.getStatus() == Status.SUCCESS) {
                    return;
                }
                url.setStatus(Status.LOCK);
                url.setUpdateTime(now);
                urlRepository.save(url);

            }

        }

        queue.add(request);

    }

    @Override
    public void push(Request request, Task task) {
        pushWhenNoDuplicate(request, task);
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
