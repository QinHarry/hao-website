package com.hao.website.blog.dto;

import lombok.Data;

@Data
public class Statistics {

    public Statistics(Long articles, Long comments, Long links, Long attaches) {
        this.articles = articles;
        this.comments = comments;
        this.links = links;
        this.attaches = attaches;
    }

    private Long articles;
    private Long comments;
    private Long links;
    private Long attaches;
}
