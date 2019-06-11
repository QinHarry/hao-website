package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 21:54 12/04/2018
 **/
public interface ContentJPA extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content> {

    Content findByContent(String content);

    @Query(value = "SELECT COUNT(*) from t_content_meta WHERE content_id = ?1 AND meta_id = ?2", nativeQuery = true)
    Integer countByContentIdAndMetaId(Integer contentId, Integer MetaId);


    Optional<Content> findContentById(Integer id);

    long countByStatusAndType(String status, String type);

    void deleteById(int id);
}
