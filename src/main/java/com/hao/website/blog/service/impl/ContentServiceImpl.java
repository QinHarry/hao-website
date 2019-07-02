package com.hao.website.blog.service.impl;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dao.ContentJPA;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.service.IContentService;
import com.hao.website.blog.service.IMetaService;
import com.hao.website.blog.utils.TaleUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
public class ContentServiceImpl implements IContentService {

    @Autowired
    private ContentJPA contentJPA;

    @Autowired
    private IMetaService metaService;

    @Override
    @Transactional
    public String publish(Content content) {
        if (null == content) {
            return "The article is empty";
        }
        if (StringUtils.isBlank(content.getTitle())) {
            return "Title of the article is empty";
        }
        if (StringUtils.isBlank(content.getContent())) {
            return "Content of the article is empty";
        }
        if (StringUtils.isBlank(content.getImg())) {
            return "The URL of image is empty";
        }
        if (content.getTitle().length() > WebConstant.MAX_TITLE_COUNT) {
            return "The title length is too long";
        }
        if (content.getContent().length() > WebConstant.MAX_TEXT_COUNT) {
            return "The content length is too long";
        }
        if (null == content.getAuthor()) {
            return "Please sign in";
        }

        if (content.getType().equals(Types.PAGE.getType())) {
            if (StringUtils.isBlank(content.getImg())
                    || !TaleUtils.testUrlWithTimeOut(content.getImg(), 500)) {
                return "The img url is invalid or unconnectable";
            }
        }

        content.setLanguage(BaseController.LANGUAGE);
        content.setContent(EmojiParser.parseToAliases(content.getContent()));
        contentJPA.save(content);

        Set<Meta> metaList = new HashSet<>();

        if (null == content.getId()) {
            metaList.addAll(metaService.generateMetas(content.getCategories(), Types.CATEGORY.getType()));
        }
        metaList.addAll(metaService.generateMetas(content.getTags(), Types.TAG.getType()));
        if (!metaList.isEmpty()) {
            metaService.saveMetas(metaList);
        }

        return WebConstant.SUCCESS_RESULT;
    }

    @Override
    public Page<Content> getContents(Integer page, Integer pageSize) {
        return getSortedContents(page, pageSize, Sort.Direction.DESC, Types.PUBLISH.getType(), Types.ARTICLE.getType());
    }

    @Override
    public Optional<Content> getContent(int id) {
        return contentJPA.findContentById(id);
    }


    @Override
    public void save(Content content) {
        contentJPA.save(content);
    }

    @Override
    public String deleteById(int id) {
        Optional<Content> content = contentJPA.findContentById(id);
        if (content.isPresent()) {
            // TODO if t_content_meta is also deleted?
            contentJPA.deleteById(id);
            return WebConstant.SUCCESS_RESULT;
        }
        return "No such post";
    }

    @Override
    public Page<Content> getSortedContents(Integer page,
                                           Integer pageSize,
                                           Sort.Direction direction,
                                           String status, String type) {
        Pageable pageable = PageRequest.of(page, pageSize, direction, "id");
        Specification<Content> specification = (Root<Content> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) -> {
            List<Predicate> list = new ArrayList<>();

            if (type != null) {
                list.add(cb.equal(root.get("type").as(String.class), type));
            }
            if (status != null) {
                list.add(cb.equal(root.get("status").as(String.class), status));
            }

            list.add(cb.equal(root.get("language").as(String.class), BaseController.LANGUAGE));

            return cb.and(list.toArray(new Predicate[list.size()]));
        };

        return contentJPA.findAll(specification, pageable);
    }
}
