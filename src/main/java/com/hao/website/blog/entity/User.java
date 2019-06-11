package com.hao.website.blog.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 21:06 12/04/2018
 **/
@Entity
@Data
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String homeUrl;

    private String screenName;

    @CreatedDate
    private Timestamp created;

    private Timestamp activated;

    private Timestamp logged;

    private String groupName;
}
