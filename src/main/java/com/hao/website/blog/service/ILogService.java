package com.hao.website.blog.service;

import com.hao.website.blog.entity.Log;
import org.springframework.data.domain.Page;

public interface ILogService {

    void insertLog(Log log);

    void insertLog(String action, String data, String ip, Integer authorId);

    Page<Log> getLogs(int page, int pageSize);
}
