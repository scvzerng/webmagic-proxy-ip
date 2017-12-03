package com.zero.webmagic;

import com.zero.webmagic.dao.IpRepository;
import com.zero.webmagic.entity.Ip;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/3-13:56
 * Project:webmagic-demo
 * Package:com.zero.webmagic
 * To change this template use File | Settings | File Templates.
 */
@Component
public class JDBCProxyProvider implements ProxyProvider {
    @Resource
    IpRepository ipRepository;
    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
        if(page==null||!page.isDownloadSuccess()){

            Ip ip = ipRepository.findByIpAndPort(proxy.getHost(),proxy.getPort());
            ip.setCanUse(false);
            ip.setFailCount(ip.getFailCount()==null?0:ip.getFailCount()+1);
            ipRepository.save(ip);
        }
    }

    @Override
    public Proxy getProxy(Task task) {
        int offset = new Random().nextInt((int) ipRepository.countByCanUseIsTrue());
        Ip ip = ipRepository.randomIp(offset);

        if(ip==null){
            return null;
        }
        return new Proxy(ip.getIp(),ip.getPort());
    }

}
