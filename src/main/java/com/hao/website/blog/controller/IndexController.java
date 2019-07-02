package com.hao.website.blog.controller;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.dto.ErrorCode;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.service.ICommentService;
import com.hao.website.blog.service.IContentService;
import com.hao.website.blog.service.IMetaService;
import com.hao.website.blog.utils.IPKit;
import com.hao.website.blog.utils.TaleUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @program: blog
 * @description: HomePage
 * @author: Hao Qin
 * @create: 11:32 12/02/2018
 **/
@Slf4j
@Controller
public class IndexController extends BaseController {

    private static final int PAGESIZE = 3;
    private static final int TAGSIZE = 10;

    @Autowired
    private IContentService contentService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IMetaService metaService;

    @GetMapping(value = {"/", "/home"})
    public String index(HttpServletRequest request) {
        Page<Content> pages = contentService.getSortedContents(
                0, PAGESIZE, Sort.Direction.DESC, null, Types.PAGE.getType());
        pages.getContent().forEach(p -> p.setContent(p.getContent().substring(0, 200) + "..."));
        request.setAttribute("pages", pages);
        Page<Content> posts = contentService.getContents(0, PAGESIZE);
        posts.getContent().forEach(p -> p.setContent(p.getContent().substring(0, 100) + "..."));
        request.setAttribute("posts", posts);
        return this.render("index");
    }

    @GetMapping(value = "page/{page}")
    public String index(HttpServletRequest request, @PathVariable int page,
                        @RequestParam(value = "pageSize", defaultValue = "12") int pageSize) {
        page = page <= 0 || page > WebConstant.MAX_PAGE ? 1 : page;
        Page<Content> articles = contentService.getContents(page - 1, pageSize);
        request.setAttribute("articles", articles);
        if (page > 1) {
            this.title(request, page + " page");
        }
        return this.render("index");
    }

    @GetMapping(value = "blog")
    public String blog(HttpServletRequest request, @RequestParam(value = "pageSize", defaultValue = "12") int pageSize) {
        return this.blog(request, 1, pageSize);
    }

    @GetMapping(value = "blog/{page}")
    public String blog(HttpServletRequest request, @PathVariable int page,
                        @RequestParam(value = "pageSize", defaultValue = "12") int pageSize) {
        page = page <= 0 || page > WebConstant.MAX_PAGE ? 1 : page;
        Page<Content> articles = contentService.getContents(page - 1, pageSize);
        articles.getContent().forEach(p -> p.setContent(p.getContent().substring(0, 100) + "..."));
        request.setAttribute("articles", articles);
        Map<String, Sort.Direction> orderMap = new HashMap<>();
        orderMap.put("id", Sort.Direction.DESC);
        List<Meta> categories = metaService.getMetaList(Types.CATEGORY.getType(), orderMap, TAGSIZE);
        List<Meta> tags = metaService.getMetaList(Types.TAG.getType(), orderMap, TAGSIZE);
        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        if (page > 1) {
            this.title(request, page + " page");
        }
        return this.render("blog");
    }

    @GetMapping(value = {"article/{id}"})
    public String getArticle(HttpServletRequest request, @PathVariable Integer id) {
        Optional<Content> content = contentService.getContent(id);
        if (!content.isPresent() || Types.DRAFT.getType().equals(content.get().getStatus())) {
            return this.render_404();
        }
        Map<String, Sort.Direction> orderMap = new HashMap<>();
        orderMap.put("id", Sort.Direction.DESC);
        List<Meta> categories = metaService.getMetaList(Types.CATEGORY.getType(), orderMap, TAGSIZE);
        List<Meta> tags = metaService.getMetaList(Types.TAG.getType(), orderMap, TAGSIZE);
        fetchComments(request, content.get());
        if (!checkHitsFrequency(request, content.get().getId())) {
            updateArticleHit(content.get());
        }
        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        request.setAttribute("article", content.get());
        request.setAttribute("is_post", true);
        return this.render("post");
    }

    @GetMapping(value = "logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session, response);
    }

    @PostMapping(value = "comment")
    @ResponseBody
    public RestResponse comment(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam Integer cid, @RequestParam Integer coid,
                                @RequestParam String author, @RequestParam String email,
                                @RequestParam String text, @RequestParam String _csrf_token) {
        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            return RestResponse.fail(ErrorCode.BAD_REQUEST);
        }
        String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);
        if (StringUtils.isBlank(token)) {
            return RestResponse.fail(ErrorCode.BAD_REQUEST);
        }
        if (null == cid || StringUtils.isBlank(text)) {
            return RestResponse.fail("Comment is missing");
        }
        Optional<Content> content = contentService.getContent(cid);
        if (!content.isPresent()) {
            return RestResponse.fail("Content is missing");
        }
        if (StringUtils.isNotBlank(author) && author.length() > 50) {
            return RestResponse.fail("Author name is too long");
        }
        if (StringUtils.isNotBlank(email) && !TaleUtils.isEmail(email)) {
            return RestResponse.fail("The email format is incorrect");
        }
        if (text.length() > 200) {
            return RestResponse.fail("The comment is too long, less than 200");
        }

        String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return RestResponse.fail("Comment is too fast, please wait a few seconds");
        }

        author = TaleUtils.cleanXSS(author);
        text = TaleUtils.cleanXSS(text);

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setContent(content.get());
        comment.setIp(request.getRemoteAddr());
        comment.setComment(text);
        comment.setEmail(email);
        comment.setParent(coid);
        comment.setStatus("not_audit");
        try {
            commentService.save(comment);
            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_email", URLEncoder.encode(email, "UTF-8"), 7 * 24 * 60 * 60, response);
            // One post only can be comment once in one minute
            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);
            return RestResponse.ok();
        } catch (Exception e) {
            String msg = "Fail to comment";
            log.error(msg, e);
            return RestResponse.fail(msg);
        }
    }

    @RequestMapping(value = "lan")
    @ResponseBody
    public RestResponse changeLAN(HttpServletRequest request, @RequestParam String lan) {
        BaseController.LANGUAGE = lan;
        return RestResponse.ok();
    }

    /** fetch comments for the given post
     * @param request
     * @param content
     */
    private void fetchComments(HttpServletRequest request, Content content) {
        if (content.isAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            Page<Comment> comments = commentService.getComments(content, Integer.parseInt(cp), 6);
            request.setAttribute("comments", comments.getContent());
        }
    }

    /** If a post is read by the same ip in past 2 hours
     * @param request
     * @param contentId
     * @return
     */
    private boolean checkHitsFrequency(HttpServletRequest request, Integer contentId) {
        String val = IPKit.getIpAddrByRequest(request) + ":" + contentId;
        Integer count = cache.hget(Types.HITS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return true;
        }
        cache.hset(Types.HITS_FREQUENCY.getType(), val, 1, WebConstant.HITS_LIMIT_TIME);
        return false;
    }

    private void updateArticleHit(Content content) {
        if (content == null) {
            return;
        }
        Integer hits = cache.hget("article" + content.getId(), "hits");
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConstant.HIT_EXCEED) {
            content.setHits(hits + content.getHits());
            contentService.save(content);
            cache.hset("article" + content.getId(), "hits", 1);
        } else {
            cache.hset("article" + content.getId(), "hits", hits);
        }
    }

    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}
