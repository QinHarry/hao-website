package com.hao.website.blog.dao;

import com.hao.website.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserJPA extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findUserById(Integer id);

    Optional<User> findUserByUsernameAndPassword(String username, String password);
}
