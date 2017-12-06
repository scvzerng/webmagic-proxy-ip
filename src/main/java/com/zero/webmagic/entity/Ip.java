package com.zero.webmagic.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-18:22
 * Project:webmagic-demo
 * Package:com.zero.webmagic.entity
 * To change this template use File | Settings | File Templates.
 */
@Data
@Entity
public class Ip implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String ip;
    private Integer port;
    private String city;
    private Boolean isOpen = false;
    private String type;
    private String speed;
    private String connectTime;
    private String aliveTime;
    private LocalDateTime checkTime = LocalDateTime.now();
    private Boolean canUse = false;
    private Integer failCount = 0;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
    @OneToMany(mappedBy ="url",fetch = FetchType.EAGER)
    private Set<Url> urls;
}
