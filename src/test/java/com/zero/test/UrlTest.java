package com.zero.test;
import com.zero.webmagic.Application;
import com.zero.webmagic.dao.UrlRepository;
import com.zero.webmagic.entity.Url;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * description
 * <p>
 * 2017-12-05 13:37
 *
 * @author scvzerng
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class UrlTest {
    @Resource
    UrlRepository repository;

    @Test
    public void testUrl(){
        Url url = new Url();
        url.setUrl("aaa.ccc");
        Url parent = new Url();
        parent.setId(1L);
        url.setParent(parent);
        repository.save(url);
    }
}
