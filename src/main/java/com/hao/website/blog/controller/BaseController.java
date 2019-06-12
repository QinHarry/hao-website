package com.hao.website.blog.controller;

import com.hao.website.blog.entity.User;
import com.hao.website.blog.utils.MapCache;
import com.hao.website.blog.utils.TaleUtils;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 22:01 12/04/2018
 **/
public class BaseController {

    public static String THEME = "themes/distribution";

    public static String LANGUAGE = "EN";

    protected MapCache cache = MapCache.single();

    public String render(String viewName) {
        return THEME + "/" + viewName;
    }

    public BaseController title(HttpServletRequest request, String title) {
        request.setAttribute("title", title);
        return this;
    }

    public BaseController keywords(HttpServletRequest request, String keywords) {
        request.setAttribute("keywords", keywords);
        return this;
    }

    public User user(HttpServletRequest request) {
        return TaleUtils.getLoginUser(request);
    }

    public Integer getUid(HttpServletRequest request) {
        return this.user(request).getId();
    }

    public String render_404() {
        return "common/error_404";
    }
}
