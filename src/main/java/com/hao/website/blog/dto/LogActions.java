package com.hao.website.blog.dto;

public enum LogActions {

    LOGIN("Admin login"),
    UP_PWD("Update password"),
    UP_INFO("Update user info"),
    DEL_ARTICLE("Delete post"),
    DEL_PAGE("Delete page"),
    SYS_BACKUP("System backup"),
    SYS_SETTING("System setting"),
    INIT_SITE("Initial site"),
    DEL_FILE("Delete a file");


    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    LogActions(String action) {
        this.action = action;
    }
}
