package com.zero.webmagic.entity;

import com.zero.webmagic.enums.Status;
import lombok.Data;

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
    private Status status = Status.LOCK;
    @OneToMany(mappedBy = "ip", fetch = FetchType.EAGER)
    private Set<Ip> ips;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Url parent;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
}
