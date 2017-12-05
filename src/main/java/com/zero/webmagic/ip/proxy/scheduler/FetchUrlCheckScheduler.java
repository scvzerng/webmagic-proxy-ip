package com.zero.webmagic.ip.proxy.scheduler;

import com.zero.webmagic.dao.UrlRepository;
import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.FetchStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description
 * <p>
 * 2017-12-05 17:22
 *
 * @author scvzerng
 **/
@Slf4j
@Component
public class FetchUrlCheckScheduler {

    @Resource
    UrlRepository urlRepository;

    /**
     * 初始化解锁所有锁定的URL
     */
    @PostConstruct
    public void init(){
        List<Url> lockUrl = urlRepository.findUrlsByStatusIn(FetchStatusEnum.LOCK);
        urlRepository.saveAll(lockUrl
                .stream()
                .parallel()
                .peek(url-> {
                    log.info("{} status from {} to {}",url.getUrl(),url.getStatus(),FetchStatusEnum.FAIL);
                    url.setStatus(FetchStatusEnum.FAIL);
                })
                .collect(Collectors.toList()));
    }
}
