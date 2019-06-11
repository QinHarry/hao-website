package com.hao.website.blog.service;

import com.hao.website.blog.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @program: blog
 * @description: blog content
 * @author: Hao Qin
 * @create: 23:28 12/02/2018
 **/
public interface IContentService {

    String publish(Content contents);

    Page<Content> getContents(Integer page, Integer pageSize);

    Optional<Content> getContent(int id);

    void save(Content content);

    String deleteById(int id);

    Page<Content> getSortedContents(Integer page, Integer pageSize,
                                    Sort.Direction direction, String status, String type);
}
