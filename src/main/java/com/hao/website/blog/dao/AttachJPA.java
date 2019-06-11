package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AttachJPA extends JpaRepository<Attach, Integer>, JpaSpecificationExecutor<Attach> {

    Optional<Attach> findById(Integer id);
}
