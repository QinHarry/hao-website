package com.hao.website.blog.dao;

import entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 21:54 12/04/2018
 **/
public interface ContentJPA extends JpaRepository<Content, Integer> {
}
