package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.LogActions;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.dto.Statistics;
import com.hao.website.blog.entity.Comment;
import com.hao.website.blog.entity.Content;
import com.hao.website.blog.entity.Log;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.exception.TipException;
import com.hao.website.blog.service.ILogService;
import com.hao.website.blog.service.ISiteService;
import com.hao.website.blog.service.IUserService;
import com.hao.website.blog.utils.GsonUtils;
import com.hao.website.blog.utils.TaleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller("adminIndexController")
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class IndexController extends BaseController {

    @Autowired
    private ILogService logService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISiteService siteService;

    @GetMapping(value = {"", "/index"})
    public String index(HttpServletRequest request) {
        List<Comment> comments = siteService.recentComments(5);
        List<Content> contents = siteService.recentContents(5);
        Statistics statistics = siteService.getStatistics();
        List<Log> logs = logService.getLogs(1, 5).getContent();

        request.setAttribute("comments", comments);
        request.setAttribute("articles", contents);
        request.setAttribute("statistics", statistics);
        request.setAttribute("logs", logs);
        return "/admin/index";
    }

    @GetMapping(value = "/profile")
    public String profile() {
        return "/admin/profile";
    }

    @PostMapping(value = "/profile")
    @ResponseBody
    public RestResponse saveProfile(@RequestParam String screenName,
                                    @RequestParam String email,
                                    HttpServletRequest request,
                                    HttpSession session) {
        User user = this.user(request);
        if (StringUtils.isNotBlank(screenName) && StringUtils.isNotBlank(email)) {
            user.setScreenName(screenName);
            user.setEmail(email);
            userService.save(user);
            logService.insertLog(LogActions.UP_INFO.getAction(),
                    GsonUtils.toJsonString(user), request.getRemoteAddr(), this.getUid(request));

            User original = (User) session.getAttribute(WebConstant.LOGIN_SESSION_KEY);
            original.setScreenName(screenName);
            original.setEmail(email);
            session.setAttribute(WebConstant.LOGIN_SESSION_KEY, original);
        }
        return RestResponse.ok();
    }

    @PostMapping(value = "/password")
    @ResponseBody
    public RestResponse upPwd(@RequestParam String oldPassword,
                              @RequestParam String password,
                              HttpServletRequest request,
                              HttpSession session) {
        User user = this.user(request);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            return RestResponse.fail("Incorrect input");
        }
        if (!user.getPassword().equals(TaleUtils.MD5encode(user.getUsername() + oldPassword))) {
            return RestResponse.fail("Incorrect old password");
        }
        if (password.length() < 6 || password.length() > 14) {
            return RestResponse.fail("The length of password should be 6-14");
        }
        try {
            String pwd = TaleUtils.MD5encode(user.getUsername() + password);
            user.setPassword(pwd);
            userService.save(user);
            logService.insertLog(LogActions.UP_PWD.getAction(),
                    null, request.getRemoteAddr(), this.getUid(request));
            User original = (User) session.getAttribute(WebConstant.LOGIN_SESSION_KEY);
            original.setPassword(pwd);
            session.setAttribute(WebConstant.LOGIN_SESSION_KEY, original);
            return RestResponse.ok();
        } catch (Exception e) {
            String msg = "Fail to update password";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                log.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
    }

}
