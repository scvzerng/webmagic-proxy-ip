package com.zero.webmagic.dao;

import com.zero.webmagic.entity.Url;
import com.zero.webmagic.enums.FetchStatusEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * description
 * <p>
 * 2017-12-05 10:36
 *
 * @author scvzerng
 **/
public interface UrlRepository extends CrudRepository<Url,Long> {
     Url findUrlByUrlAndStatusIn(String url, FetchStatusEnum... statusEnum);
     List<Url> findUrlsByStatusIn(FetchStatusEnum... statusEnum);
    Url findUrlByUrl(String url);

    List<Url> findUrlsByUrlIn(List<String> urls);

    List<Url> findUrlsByParentIdAndStatusIn(Long id,FetchStatusEnum... statusEnum);
}
