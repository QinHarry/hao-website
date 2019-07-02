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
import lombok.extern.slf4j.Slf4j;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SiteServiceImpl implements ISiteService {

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
    public boolean fileBackup(final String path) {
        String filename = "tale_" + DateKit.dateFormat(new Date(), "yyyyMMddHHmm") + ".tar.gz";
        String savePath;
        if (!path.endsWith(File.separator)) {
            savePath = path + File.separator;
        } else {
            savePath = path;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("tar -zcvf ").append(savePath + filename).append(" ");
        sb.append(AttachController.CLASSPATH + "upload/");
        return baseBackup(savePath, filename, sb.toString());
    }

    @Override
    public boolean databaseBackup(final String path) {
        String sqlFileName = "tale_" + DateKit.dateFormat(
                new Date(), "yyyyMMddHHmm") + "_" + TaleUtils.getRandomNumber(5) + ".sql";
        String savePath;
        if (!path.endsWith(File.separator)) {
            savePath = path + File.separator;
        } else {
            savePath = path;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mysqldump").append(" --opt").append(" -h").append(" localhost");
        stringBuilder.append(" --user=").append("hao").append(" --password=").append("1122");
        stringBuilder.append(" --lock-all-tables=true");
        stringBuilder.append(" --result-file=").append(savePath + sqlFileName);
        stringBuilder.append(" --default-character-set=utf8 ").append("tale");

        return baseBackup(savePath, sqlFileName, stringBuilder.toString());
    }

    private synchronized boolean baseBackup(final String path, final String name, final String command) {
        boolean res = false;

        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;

        File saveFile = new File(path);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(path + name), StandardCharsets.UTF_8));
            Process process = Runtime.getRuntime().exec(command);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(),
                    StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                printWriter.println(line);
            }
            printWriter.flush();
            if (process.waitFor() == 0) {
                res = true;
            }
        } catch (FileNotFoundException e) {
            log.info("backup file not found ", e);
        } catch (InterruptedException ee) {
            log.info("backup interrupted ", ee);
        } catch (IOException eee) {
            log.info("Failed to write ", eee);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                log.info("Failed to close backup", e);
            }
        }
        return res;
    }
}
