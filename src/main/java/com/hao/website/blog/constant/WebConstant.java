package com.hao.website.blog.constant;

import java.util.HashMap;
import java.util.Map;

public class WebConstant {

    public static Map<String, String> initConfig = new HashMap<>();

    public static String LOGIN_SESSION_KEY = "login_user";

    public static final String USER_IN_COOKIE = "S_L_ID";

    public static String AES_SALT = "0123456789abcdef";

    public static final int MAX_POSTS = 9999;

    public static final int MAX_PAGE = 100;

    public static final int MAX_TEXT_COUNT = 200000;

    public static final int MAX_TITLE_COUNT = 200;

    public static final int HIT_EXCEED = 10;

    public static Integer MAX_FILE_SIZE = 1048576;

    public static String SUCCESS_RESULT = "SUCCESS";

    // Don't count the read number if the article was already read in 2 hours
    public static Integer HITS_LIMIT_TIME = 7200;
}
