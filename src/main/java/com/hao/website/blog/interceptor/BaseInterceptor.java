package com.hao.website.blog.interceptor;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Option;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.service.IOptionService;
import com.hao.website.blog.service.IUserService;
import com.hao.website.blog.utils.AdminCommon;
import com.hao.website.blog.utils.ViewUtil;
import com.hao.website.blog.utils.IPKit;
import com.hao.website.blog.utils.MapCache;
import com.hao.website.blog.utils.TaleUtils;
import com.hao.website.blog.utils.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);
    private static final String USER_AGENT = "user-agent";
    private static List<String> staticUrls = Arrays.asList("/js/", "/css/", "/images/", "/plugins/", "/fonts/",
            "/icons-reference/", "/img/", "/vendor/");

    @Autowired
    private IUserService userService;

    @Autowired
    private IOptionService optionService;

    @Autowired
    private ViewUtil viewUtil;

    @Autowired
    private AdminCommon adminCommon;

    private MapCache cache = MapCache.single();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();

        logger.info("UserAgent: {}", request.getHeader(USER_AGENT));
        logger.info("Request to: {}, request from {}", uri, IPKit.getIpAddrByRequest(request));

        User user = TaleUtils.getLoginUser(request);
        if (null == user) {
            Integer userId = TaleUtils.getCookieUid(request);
            if (null != userId) {
                // TODO not safe, cookie could be forged.
                user = userService.findUserById(userId);
                request.getSession().setAttribute(WebConstant.LOGIN_SESSION_KEY, user);
            }
        }

        // If the url points a static resource, no auth check
        boolean isStatic = false;
        for (String staticUrl : staticUrls) {
            if (uri.contains(staticUrl)) {
                isStatic = true;
                break;
            }
        }

        if (!isStatic && uri.startsWith(contextPath + "/admin")
                && !uri.startsWith(contextPath + "/admin/login") && user == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }

        if (request.getMethod().equals("GET")) {
            String csrf_token = UUID.UU64();
            cache.hset(Types.CSRF_TOKEN.getType(), csrf_token, uri, 30 * 60);
            request.setAttribute("_csrf_token", csrf_token);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Option option = optionService.getOptionByName("site_record");
        request.setAttribute("common", viewUtil);
        request.setAttribute("option", option);
        request.setAttribute("adminCommon", adminCommon);
        request.setAttribute("lan", BaseController.LANGUAGE);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
