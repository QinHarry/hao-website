package com.hao.website.blog.service.impl;

import com.hao.website.blog.dao.UserJPA;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.IContentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentServiceImplTest {

    Logger logger = LoggerFactory.getLogger(ContentServiceImplTest.class);

    @Autowired
    private IContentService contentService;
    @Autowired
    private UserJPA userJPA;

    private Content content;

    @Before
    public void init() {
        content = new Content();
        content.setTitle("test2");
        User user = userJPA.findById(2).get();
        content.setContent("Test content22222222");
        content.setAuthor(user);
        content.setTags("tag222");
        content.setType(Types.ARTICLE.getType());
        content.setStatus(Types.PUBLISH.getType());
        content.setCategories("category222");
    }

    @Test
    public void publishAndGet() {
        String res = contentService.publish(content);
        Page<Content> contents = contentService.getContents(0, 10);
        assertEquals(2, contents.getContent().size());
    }

}