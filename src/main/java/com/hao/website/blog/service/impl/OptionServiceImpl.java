package com.hao.website.blog.service.impl;

import com.hao.website.blog.dao.OptionJPA;
import com.hao.website.blog.entity.Option;
import com.hao.website.blog.service.IOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OptionServiceImpl implements IOptionService {

    @Autowired
    private OptionJPA optionJPA;

    @Override
    public Option getOptionByName(String name) {
        return optionJPA.findByName(name);
    }

    @Override
    public List<Option> getOptions() {
        return optionJPA.findAll();
    }

    @Override
    public void save(Map<String, String> map) {
        map.forEach(this::save);
    }

    @Override
    @Transactional
    public void save(String name, String value) {
        Option option = new Option();
        option.setName(name);
        option.setValue(value);
        optionJPA.save(option);
    }
}
