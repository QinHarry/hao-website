package com.hao.website.blog.service;

import com.hao.website.blog.entity.Option;

import java.util.List;
import java.util.Map;

public interface IOptionService {

    Option getOptionByName(String name);

    List<Option> getOptions();

    void save(Map<String, String> map);

    void save(String name, String value);
}
