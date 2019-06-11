package com.hao.website.blog.service;

import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ICommentService {

    Page<Comment> getComments(Content content, Integer page, Integer pageSize);

    void save(Comment comment);

    Page<Comment> getComments(User user, Integer page, Integer pageSize);

    Optional<Comment> getComment(Integer id);

    String deleteById(Integer id);
}
