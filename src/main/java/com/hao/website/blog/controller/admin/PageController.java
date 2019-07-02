package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.LogActions;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.IContentService;
import com.hao.website.blog.service.ILogService;
import com.hao.website.blog.service.IMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
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

@Slf4j
@Controller
@RequestMapping("/admin/page")
public class PageController extends BaseController {

    @Autowired
    private IContentService contentService;

    @Autowired
    private IMetaService metaService;

    @Autowired
    private ILogService logService;

    @GetMapping(value = "")
    public String index(HttpServletRequest request) {
        Page<Content> pages = contentService.getSortedContents(
                0, WebConstant.MAX_POSTS, Sort.Direction.DESC, null, Types.PAGE.getType());
        request.setAttribute("pages", pages);
        return "admin/page_list";
    }

    @GetMapping(value = "/new")
    public String newPage(HttpServletRequest request) {
        List<Meta> categories = metaService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/page_edit";
    }

    @GetMapping(value = "/{id}")
    public String editPage(@PathVariable Integer id, HttpServletRequest request) {
        Optional<Content> content = contentService.getContent(id);
        if (content.isPresent()) {
            request.setAttribute("contents", content.get());
            List<Meta> categories = metaService.getMetas(Types.CATEGORY.getType());
            request.setAttribute("categories", categories);
        }
        return "admin/page_edit";
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponse publishPage(@RequestBody Content content, HttpServletRequest request) {
        User user = this.user(request);
        content.setAuthor(user);
        content.setType(Types.PAGE.getType());
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
        logService.insertLog(LogActions.DEL_PAGE.getAction(),
                id + "", request.getRemoteAddr(), this.getUid(request));
        if (!WebConstant.SUCCESS_RESULT.equals(result)) {
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }
}
