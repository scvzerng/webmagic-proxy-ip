package com.zero.webmagic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-22:00
 * Project:webmagic-demo
 * Package:com.zero.webmagic.entity
 * To change this template use File | Settings | File Templates.
 */
@Data
@AllArgsConstructor
public class ScanIp {
    private final int first;
    private final int second;
    private final int three;
    private final int four;
    private final int port;
    private final String point = ".";

    public String getIp(){
        return first+point+second+point+three+point+four;
    }
}
