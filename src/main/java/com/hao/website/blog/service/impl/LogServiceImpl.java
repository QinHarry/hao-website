package com.hao.website.blog.service.impl;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.dao.LogJPA;
import com.hao.website.blog.dao.UserJPA;
import com.hao.website.blog.entity.Log;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.ILogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LogServiceImpl implements ILogService {

    @Autowired
    private LogJPA logJPA;

    @Autowired
    private UserJPA userJPA;

    @Override
    public void insertLog(Log log) {
        logJPA.save(log);
    }

    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        Log log = new Log();
        log.setAction(action);
        log.setData(data);
        log.setIp(ip);
        Optional<User> user = userJPA.findById(authorId);
        user.ifPresent(u -> log.setUser(u));
        logJPA.save(log);
    }

    @Override
    public Page<Log> getLogs(int page, int pageSize) {
        if (page <= 0) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > WebConstant.MAX_POSTS) {
            pageSize = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id");
        return logJPA.findAll(pageable);
    }
}
