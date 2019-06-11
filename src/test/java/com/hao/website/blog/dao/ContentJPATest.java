package com.hao.website.blog.dao;

import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.Meta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentJPATest {

    @Autowired
    private ContentJPA contentJPA;
    @Autowired
    private MetaJPA metaJPA;

    @Before
    public void init() {
        Meta meta1 = new Meta();
        meta1.setName("meta1");
        meta1.setType("test");

        Meta meta2 = new Meta();
        meta2.setName("meta2");
        meta2.setType("test");

        Meta meta3 = new Meta();
        meta3.setName("meta3");
        meta3.setType("test");

        Content content1 = new Content();
        content1.setContent("Spring in Action");

        Content content2 = new Content();
        content2.setContent("Spring Boot in Action");

        contentJPA.saveAll(Arrays.asList(content1, content2));
    }

    @After
    public void deleteAll() {
        contentJPA.deleteAll();
        metaJPA.deleteAll();
    }

    @Test
    public void findAll() {
        assertEquals(2, contentJPA.findAll().size());
        assertEquals(3, metaJPA.findAll().size());
    }

    @Test
    public void findByContent() {
        assertNotNull(contentJPA.findByContent("Spring in Action"));
        assertNotNull(metaJPA.findByName("meta1"));
    }

//    @Test
//    public void countByContentIdAndMetaId() {
//        assertEquals(1, (long)contentJPA.countByContentIdAndMetaId(182, 200));
//    }

//    @Test
//    @Transactional
//    public void findAllContentOfMeta() {
//        Meta meta = metaJPA.findByName("meta2");
//        Set<Content> contents = meta.getContents();
//        assertNotNull(contents);
//        //assertEquals(2, contents.size());
//    }
}