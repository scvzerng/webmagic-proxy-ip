package com.zero.webmagic.core;

import com.alibaba.fastjson.JSON;
import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * 代理IP持久化至数据库
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
    private
    IpRepository ipRepository;

    @Transactional(dontRollbackOn = Exception.class, value = Transactional.TxType.REQUIRED)
    public void process(ResultItems resultItems, Task task) {
        List<Ip> ips = resultItems.get("ips");
        Optional.ofNullable(ips).ifPresent(exist ->
                ips.stream().
                        filter(ip ->
                                Objects.isNull(ipRepository.findByIp(ip.getIp())))
                        .forEach(ip -> {
                            try {
                                synchronized (ipRepository) {
                                    ipRepository.save(ip);
                                }
                            } catch (Exception e) {
                                log.error("{} saved error {}", JSON.toJSONString(ip), e.getMessage());
                            }
                        }));

    }
}
