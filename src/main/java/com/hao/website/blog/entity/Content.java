package com.hao.website.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 21:17 12/04/2018
 **/
@Entity
@Data
@Table(name = "t_content")
public class Content extends BaseEntity{

    private String title;

    @ManyToOne
    @JoinColumn(name = "authorId")
    @JsonIgnore
    private User author;

    private String type;

    private String language;

    private String status;

    private String tags;

    private String categories;

    private int hits;

    private int commentsNum;

    private boolean allowComment;

    private boolean allowPing;

    private boolean allowFeed;

    private String content;

    private String img;
}
