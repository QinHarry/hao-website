package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OptionJPA extends JpaRepository<Option, String>, JpaSpecificationExecutor<Option> {

    Option findByName(String name);
}
