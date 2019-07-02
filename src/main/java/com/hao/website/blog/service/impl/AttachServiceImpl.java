package com.hao.website.blog.service.impl;

import com.hao.website.blog.dao.AttachJPA;
import com.hao.website.blog.entity.Attach;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.IAttachService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class AttachServiceImpl implements IAttachService {

    @Autowired
    private AttachJPA attachJPA;

    @Override
    public Page<Attach> getAttachs(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id");
        return attachJPA.findAll(pageable);
    }

    @Override
    public Optional<Attach> getById(Integer id) {
        return attachJPA.findById(id);
    }

    @Override
    @Transactional
    public void save(String fileName, String fileKey, String fileType, User user) {
        Attach attach = new Attach(user, fileName, fileType, fileKey);
        attachJPA.save(attach);
    }

    @Override
    public void deleteById(Integer id) {
        attachJPA.deleteById(id);
    }
}
