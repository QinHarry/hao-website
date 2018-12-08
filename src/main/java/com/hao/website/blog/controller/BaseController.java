package com.hao.website.blog.controller;

import com.hao.website.blog.utils.MapCache;
import org.springframework.stereotype.Controller;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 22:01 12/04/2018
 **/
public class BaseController {

    public static String THEME = "themes/default";

    protected MapCache cache = MapCache.single();

    public String render(String viewName) {

    }
}
