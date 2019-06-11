package com.hao.website.blog.service.impl;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.dao.CommentJPA;
import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentJPA commentJPA;

    @Override
    public Page<Comment> getComments(Content content, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id");

        Specification<Comment> specification = (Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) -> {
            List<Predicate> list = new ArrayList<>();

            list.add(cb.equal(root.get("content").as(Content.class), content));
            //list.add(cb.equal(root.get("parent").as(Integer.class), 0));
            list.add(cb.isNotNull(root.get("status")));
            list.add(cb.equal(root.get("status").as(String.class), "approved"));

            return cb.and(list.toArray(new Predicate[list.size()]));
        };

        return commentJPA.findAll(specification, pageable);
    }

    @Override
    public void save(Comment comment) {
        commentJPA.save(comment);
    }

    @Override
    public Page<Comment> getComments(User user, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id");

        Specification<Comment> specification = (Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) ->
                cb.and(cb.notEqual(root.get("user").as(User.class), user));

        //return commentJPA.findAll(specification, pageable);
        return commentJPA.findAll(pageable);
    }

    @Override
    public Optional<Comment> getComment(Integer id) {
        return commentJPA.findById(id);
    }

    @Override
    public String deleteById(Integer id) {
        if (commentJPA.findById(id).isPresent()) {
            commentJPA.deleteById(id);
            return WebConstant.SUCCESS_RESULT;
        }
        return "No such comment";
    }
}
