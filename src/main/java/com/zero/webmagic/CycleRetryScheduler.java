package com.zero.webmagic;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-22:18
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CycleRetryScheduler extends QueueScheduler {
    @Override
    public void push(Request request, Task task) {
        pushWhenNoDuplicate(request,task);
    }
}
