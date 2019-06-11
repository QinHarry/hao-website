package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.LogActions;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.entity.User;
import com.hao.website.blog.exception.TipException;
import com.hao.website.blog.service.ILogService;
import com.hao.website.blog.service.IUserService;
import com.hao.website.blog.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private ILogService logService;

    @GetMapping(value = "/login")
    public String login() {
        return "admin/login";
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public RestResponse doLogin(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(required = false) String remmeber_me,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        Integer errorCount = cache.get("login_error_count");
        try {
            User user = userService.login(username, password);
            request.getSession().setAttribute(WebConstant.LOGIN_SESSION_KEY, user);
            if (StringUtils.isNotBlank(remmeber_me)) {
                TaleUtils.setCookie(response, user.getId());
            }
            logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getId());
        } catch (Exception e) {
            errorCount = null == errorCount ? 1 : errorCount + 1;
            if (errorCount > 3) {
                return RestResponse.fail("The fail of login is more than 3 times, please try again 10 minutes later");
            }
            cache.set("login_error_count", errorCount, 10 * 60);
            String msg = "Login fail";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                logger.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        session.removeAttribute(WebConstant.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(WebConstant.USER_IN_COOKIE, "");
        cookie.setValue(null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        try {
            response.sendRedirect("/admin/login");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Fail to logout", e);
        }
    }
}
