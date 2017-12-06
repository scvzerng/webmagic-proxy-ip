package com.zero.webmagic.dao;

import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * description
 * <p>
 * 2017-12-05 10:36
 *
 * @author scvzerng
 **/
public interface UrlRepository extends CrudRepository<Url, Long> {
    List<Url> findUrlsByStatusIn(Status... statusEnum);

    Url findUrlByUrl(String url);

    List<Url> findUrlsByParentIdAndStatusIn(Long id, Status... statusEnum);
}
