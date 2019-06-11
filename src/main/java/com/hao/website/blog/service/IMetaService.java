package com.hao.website.blog.service;

import com.hao.website.blog.entity.Meta;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMetaService {

    Set<Meta> generateMetas(String names, String type);

    List<Meta> getMetas(String type);

    List<Meta> getMetaList(String type, Map<String, Sort.Direction> order, int limit);

    void saveMeta(String type, String name, Integer mid);

    void saveMetas(Set<Meta> metas);

    void deleteById(Integer id);

    void saveMeta(Meta meta);
}
