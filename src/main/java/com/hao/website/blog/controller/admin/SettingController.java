package com.hao.website.blog.controller.admin;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.controller.BaseController;
import com.hao.website.blog.dto.LogActions;
import com.hao.website.blog.dto.RestResponse;
import com.hao.website.blog.entity.Option;
import com.hao.website.blog.service.ILogService;
import com.hao.website.blog.service.IOptionService;
import com.hao.website.blog.service.ISiteService;
import com.hao.website.blog.utils.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/admin/setting")
public class SettingController extends BaseController {

    @Autowired
    private IOptionService optionService;

    @Autowired
    private ILogService logService;

    @Autowired
    private ISiteService siteService;


    @GetMapping(value = "")
    public String setting(HttpServletRequest request) {
        List<Option> options = optionService.getOptions();
        Map<String, String> optionMap = new HashMap<>();
        optionMap.putIfAbsent("site_title", "");
        optionMap.putIfAbsent("site_description", "");
        optionMap.putIfAbsent("site_record", "");
        optionMap.putIfAbsent("site_theme", "");
        optionMap.putIfAbsent("social_weibo", "");
        optionMap.putIfAbsent("social_github", "");
        optionMap.putIfAbsent("social_twitter", "");
        options.forEach(option -> optionMap.put(option.getName(), option.getValue()));
        request.setAttribute("options", optionMap);
        return "admin/setting";
    }

    @PostMapping(value = "")
    @ResponseBody
    public RestResponse saveSetting(@RequestParam(required = false) String site_theme, HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> querys = new HashMap<>();
            parameterMap.forEach((k, v) -> querys.put(k, join(v)));
            optionService.save(querys);
            WebConstant.initConfig = querys;
            if (StringUtils.isNotBlank(site_theme)) {
                BaseController.THEME = "themes/" + site_theme;
            }
            logService.insertLog(LogActions.SYS_SETTING.getAction(), GsonUtils.toJsonString(querys),
                    request.getRemoteAddr(), this.getUid(request));
            return RestResponse.ok();
        } catch (Exception e) {
            String msg = "Failed to save setting";
            return RestResponse.fail(msg);
        }
    }

    public RestResponse backup(@RequestParam String bk_type, @RequestParam String bk_path, HttpServletRequest request) {
        if (StringUtils.isBlank(bk_type)) {
            return RestResponse.fail("Please input backup type");
        }

        return RestResponse.ok();
    }

    /**
     * @param arr
     * @return
     */
    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < arr.length; ++i) {
            String item = arr[i];
            ret.append(',').append(item);
        }

        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }
}
