package com.hao.website.blog.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "t_option")
public class Option {

    @Id
    private String name;
    private String value;
    private String desc;
}
