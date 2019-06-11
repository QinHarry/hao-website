package com.hao.website.blog.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@Table(name = "t_meta")
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String type;

    private String description;

    private Integer sort;

    private Integer parent;

    private Integer count;

    private String slug;
}
