package com.hao.website.blog.utils;

import com.hao.website.blog.entity.Meta;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

@Component
public class AdminCommon {

    /** The intersection of category and cat
     * @param category
     * @param cats
     * @return
     */
    public static boolean exist_cat(Meta category, String cats) {
        String[] arr = StringUtils.split(cats, ",");
        if (null != arr && arr.length > 0) {
            for (String c : arr) {
                if (c.trim().equals(category.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final String[] COLORS = {"origin", "primary", "success", "info", "warning", "danger", "inverse", "purple", "pink"};

    public static String rand_color() {
        int r = TaleUtils.rand(0, COLORS.length - 1);
        return COLORS[r];
    }
}
