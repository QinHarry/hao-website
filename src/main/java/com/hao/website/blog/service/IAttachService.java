package com.hao.website.blog.service;

import com.hao.website.blog.entity.Attach;
import com.hao.website.blog.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IAttachService {

    Page<Attach> getAttachs(int page, int pageSize);

    void save(String fileName, String fileKey, String fileType, User user);

    Optional<Attach> getById(Integer id);

    void deleteById(Integer id);
}
