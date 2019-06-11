package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.LogActions;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.exception.TipException;
import com.hao.website.blog.service.IContentService;
import com.hao.website.blog.service.ILogService;
import com.hao.website.blog.service.IMetaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IContentService contentService;

    @Autowired
    private IMetaService metaService;

    @Autowired
    private ILogService logService;

    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit,
                        HttpServletRequest request) {
        Page<Content> contents = contentService.getSortedContents(
                page - 1, limit, Sort.Direction.DESC, null, Types.ARTICLE.getType());
        request.setAttribute("articles", contents);
        return "admin/article_list";
    }

    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        List<Meta> categories = metaService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    @GetMapping(value = "/{id}")
    public String editArticle(@PathVariable Integer id,
                              HttpServletRequest request) {
        Optional<Content> content = contentService.getContent(id);
        request.setAttribute("contents", content.get());
        List<Meta> categories = metaService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        return "admin/article_edit";
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponse publishArticle(@RequestBody Content content, HttpServletRequest request) {
        User user = this.user(request);
        content.setAuthor(user);
        content.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(content.getCategories())) {
            content.setCategories("default");
        }
        String result = contentService.publish(content);
        if (!WebConstant.SUCCESS_RESULT.equals(result)) {
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam int id, HttpServletRequest request) {
        String result = contentService.deleteById(id);
        logService.insertLog(LogActions.DEL_ARTICLE.getAction(),
                id + "", request.getRemoteAddr(), this.getUid(request));
        if (!WebConstant.SUCCESS_RESULT.equals(result)) {
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }
}
