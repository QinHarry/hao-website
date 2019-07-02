package com.hao.website.blog.service.impl;

import com.hao.website.blog.dao.UserJPA;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.exception.TipException;
import com.hao.website.blog.service.IUserService;
import com.hao.website.blog.utils.TaleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserJPA userJPA;

    @Override
    public User findUserById(Integer id) {
        return userJPA.findUserById(id);
    }

    @Override
    public User login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("Username or password is empty");
        }
        String pwd = TaleUtils.MD5encode(username + password);
        Optional<User> user = userJPA.findUserByUsernameAndPassword(username, pwd);
        if (!user.isPresent()) {
            throw new TipException("Username or password is incorrect");
        }
        return user.get();
    }

    @Override
    public void save(User user) {
        userJPA.save(user);
    }
}
