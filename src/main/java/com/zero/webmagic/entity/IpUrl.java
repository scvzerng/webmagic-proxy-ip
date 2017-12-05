package com.zero.webmagic.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description
 * <p>
 * 2017-12-05 10:14
 *
 * @author scvzerng
 **/
@Data
@Entity

public class IpUrl implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ip_id")
    private Ip ip;
    @ManyToOne
    @JoinColumn(name = "url_id")
    private Url url;
    private Integer failCount = 0;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
}
