package com.hao.website.blog.service;

import com.hao.website.blog.dto.BackResponse;
import com.hao.website.blog.dto.Statistics;
import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.Content;

import java.util.List;
import java.util.Optional;

public interface ISiteService {

    List<Comment> recentComments(int limit);

    List<Content> recentContents(int limit);

    Optional<Comment> getComment(int id);

    Statistics getStatistics();

    boolean databaseBackup(String dir);

    boolean fileBackup(String dir);
}
