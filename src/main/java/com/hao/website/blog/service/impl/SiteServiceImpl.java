package com.hao.website.blog.service.impl;

import com.hao.website.blog.controller.admin.AttachController;
import com.hao.website.blog.dao.AttachJPA;
import com.hao.website.blog.dao.CommentJPA;
import com.hao.website.blog.dao.ContentJPA;
import com.hao.website.blog.dao.MetaJPA;
import com.hao.website.blog.dto.BackResponse;
import com.hao.website.blog.dto.Statistics;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.exception.TipException;
import com.hao.website.blog.service.IContentService;
import com.hao.website.blog.service.ISiteService;
import com.hao.website.blog.utils.DateKit;
import com.hao.website.blog.utils.TaleUtils;
import com.hao.website.blog.utils.ZipUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SiteServiceImpl implements ISiteService {

    private static final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Autowired
    private CommentJPA commentJPA;

    @Autowired
    private ContentJPA contentJPA;

    @Autowired
    private AttachJPA attachJPA;

    @Autowired
    private MetaJPA metaJPA;

    @Autowired
    private IContentService contentService;

    @Override
    public List<Comment> recentComments(int limit) {
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        Pageable pageable = PageRequest.of(0, limit, Sort.Direction.DESC, "id");
        return commentJPA.findAll(pageable).getContent();
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Content> recentContents(int limit) {
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        return contentService.getSortedContents(
                0, limit, Sort.Direction.DESC,
                Types.PUBLISH.getType(), Types.ARTICLE.getType()).getContent();
    }

    @Override
    public Optional<Comment> getComment(int id) {
        return commentJPA.findById(id);
    }

    @Override
    public Statistics getStatistics() {
        long articles = contentJPA.countByStatusAndType(Types.PUBLISH.getType(), Types.ARTICLE.getType());
        long comments = commentJPA.count();
        long attaches = attachJPA.count();
        long links = metaJPA.countByType(Types.LINK.getType());
        return new Statistics(articles, comments, links, attaches);
    }

    @Override
    public BackResponse backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackResponse backResponse = new BackResponse();
        if (bk_type.equals("attach")) {
            if (StringUtils.isBlank(bk_path)) {
                throw new TipException("Please input the back path");
            }
            if (!new File(bk_path).isDirectory()) {
                throw new TipException("The folder is not exist");
            }
            String bkAttachDir = AttachController.CLASSPATH + "upload";
            String bkThemesDir = AttachController.CLASSPATH + "templates/themes";

            String fname = DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".zip";

            String attachPath = bk_path + "/" + "attachs_" + fname;
            String themesPath = bk_path + "/" + "themes_" + fname;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themesPath);

            backResponse.setAttachPath(attachPath);
            backResponse.setThemePath(themesPath);
        } else if (bk_type.equals("db")) {
            String bkAttachDir = AttachController.CLASSPATH + "upload/";
            if (!(new File(bkAttachDir)).isDirectory()) {
                File file = new File(bkAttachDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }

            String sqlFileName = "tale_" + DateKit.dateFormat(new Date(), fmt) + "_"
                    + TaleUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");


        }

        return backResponse;
    }
}
