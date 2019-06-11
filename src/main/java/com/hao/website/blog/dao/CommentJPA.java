package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CommentJPA extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {

    Optional<Comment> findById(int id);

    @Override
    void deleteById(Integer integer);
}
