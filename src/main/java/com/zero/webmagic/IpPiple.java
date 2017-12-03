package com.zero.webmagic;

import com.alibaba.fastjson.JSON;
import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-12:54
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
@Slf4j
public class IpPiple implements Pipeline {
    @Resource
    IpRepository ipRepository;
    public void process(ResultItems resultItems, Task task) {
        List<Ip> ips   = resultItems.get("ips");
        ips.stream().filter(ip-> Objects.isNull(ipRepository.findByIp(ip.getIp()))).forEach(ip->{
            try{
                ipRepository.save(ip);
            }catch (Exception e){
                log.error("{} saved error {}",JSON.toJSONString(ip),e.getMessage());
            }
        });
    }
}
