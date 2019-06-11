package com.hao.website.blog.utils;

import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class TaleUtilsTest {

    @Test
    public void getLoginUser() {
    }

    @Test
    public void isPath() {
    }

    @Test
    public void getCookieUid() {
    }

    @Test
    public void deAes() {
    }

    @Test
    public void isNumber() {
    }

    @Test
    public void rand() {
    }

    @Test
    public void getUploadFilePath() {
        String path = TaleUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1, path.length());
        try {
            path = java.net.URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File("");
    }

    @Test
    public void enAes() {
    }

    @Test
    public void isEmail() {
    }

    @Test
    public void cleanXSS() {
    }

    @Test
    public void logout() {
    }

    @Test
    public void MD5encode() {
        String password = "hao1122";
        String encoded = TaleUtils.MD5encode(password);
    }

    @Test
    public void setCookie() {
    }

    @Test
    public void getFileKey() {
    }

    @Test
    public void isImage() {
    }
}