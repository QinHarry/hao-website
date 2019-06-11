package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MetaJPA extends JpaRepository<Meta, Integer>, JpaSpecificationExecutor<Meta> {

    Meta findByName(String name);

    Meta getByNameAndType(String name, String type);

    long countByType(String type);
}
