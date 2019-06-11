package com.hao.website.blog.service;

import com.hao.website.blog.entity.User;

public interface IUserService {

    User findUserById(Integer id);

    User login(String username, String password);

    void save(User user);
}
