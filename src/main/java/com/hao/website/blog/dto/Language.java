package com.hao.website.blog.dto;

public enum Language {

    EN("EN"),
    CH("CH");

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    Language(String language) {
        this.language = language;
    }
}
