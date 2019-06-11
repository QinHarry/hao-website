package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface LogJPA extends JpaRepository<Log, Integer>, JpaSpecificationExecutor<Log> {
}
