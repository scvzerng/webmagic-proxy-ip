package com.zero.webmagic.entity;

import com.zero.webmagic.enums.FetchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * description
 * <p>
 * 2017-12-05 10:03
 *
 * @author scvzerng
 **/

@Entity
@Data
public class Url implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String url;
    private FetchStatusEnum status = FetchStatusEnum.LOCK;
    @OneToMany(mappedBy = "ip")
    private Set<Ip> ips;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Url parent;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
}
