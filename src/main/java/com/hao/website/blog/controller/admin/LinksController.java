package com.hao.website.blog.controller.admin;

import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.service.IMetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/links")
public class LinksController extends BaseController {

    @Autowired
    private IMetaService metaService;

    @GetMapping("")
    public String index(HttpServletRequest request) {
        List<Meta> metas = metaService.getMetas(Types.LINK.getType());
        request.setAttribute("links", metas);
        return "admin/links";
    }

    @PostMapping("/save")
    @ResponseBody
    public RestResponse saveLink(@RequestParam String title, @RequestParam String url,
                                 @RequestParam String logo, @RequestParam Integer id,
                                 @RequestParam(value = "sort", defaultValue = "0") int sort) {
        try {
            Meta meta = new Meta();
            meta.setName(title);
            meta.setSlug(url);
            meta.setDescription(logo);
            meta.setSort(sort);
            meta.setType(Types.LINK.getType());
            meta.setId(id);
            metaService.saveMeta(meta);
        } catch (Exception e) {
            String msg = "Failed to save links ";
            log.error(msg + title, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

    @RequestMapping("/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam int id) {
        try {
            metaService.deleteById(id);
        } catch (Exception e) {
            String msg = "Failed to delete links ";
            log.error(msg + id, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }
}
