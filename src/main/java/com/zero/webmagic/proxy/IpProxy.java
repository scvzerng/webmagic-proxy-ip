package com.zero.webmagic.proxy;

import com.zero.webmagic.entity.Ip;
import lombok.Getter;
import us.codecraft.webmagic.proxy.Proxy;

/**
 * description
 * <p>
 * 2017-12-06 10:23
 *
 * @author scvzerng
 **/

public class IpProxy extends Proxy {
    @Getter
    private Ip ip;
    public IpProxy(Ip ip) {
        super(ip.getIp(), ip.getPort());
        this.ip = ip;
    }

}
