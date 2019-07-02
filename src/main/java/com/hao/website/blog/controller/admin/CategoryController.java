package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.service.IMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/category")
public class CategoryController extends BaseController {

    @Autowired
    private IMetaService metaService;

    @GetMapping(value = "")
    public String index(HttpServletRequest request) {
        Map<String, Sort.Direction> emptyOrder = new HashMap<>();
        List<Meta> categories = metaService.getMetaList(Types.CATEGORY.getType(), emptyOrder, WebConstant.MAX_POSTS);
        List<Meta> tags = metaService.getMetaList(Types.TAG.getType(), emptyOrder, WebConstant.MAX_POSTS);
        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        return "admin/category";
    }

    @PostMapping(value = "/save")
    @ResponseBody
    public RestResponse saveCategory(@RequestParam String cname, @RequestParam Integer id) {
        try {
            metaService.saveMeta(Types.CATEGORY.getType(), cname, id);
        } catch (Exception e) {
            String msg = "Fail to save category";
            log.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam Integer id) {
        try {
            metaService.deleteById(id);
        } catch (Exception e) {
            String msg = "Fail to delete " + id;
            log.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

}
