package com.zero.webmagic.dao;

import com.zero.webmagic.entity.Ip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-19:03
 * Project:webmagic-demo
 * Package:com.zero.webmagic.dao
 * To change this template use File | Settings | File Templates.
 */

public interface IpRepository extends CrudRepository<Ip, Long>, PagingAndSortingRepository<Ip, Long> {
    Ip findByIp(String ip);
}
