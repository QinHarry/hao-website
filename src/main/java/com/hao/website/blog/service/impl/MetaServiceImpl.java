package com.hao.website.blog.service.impl;

import com.hao.website.blog.constant.WebConstant;
import com.hao.website.blog.dao.MetaJPA;
import com.hao.website.blog.dto.Types;
import com.hao.website.blog.entity.Meta;
import com.hao.website.blog.exception.TipException;
import com.hao.website.blog.service.IMetaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MetaServiceImpl implements IMetaService {

    @Autowired
    private MetaJPA metaJPA;

    @Override
    public Set<Meta> generateMetas(String names, String type) {
        Set<Meta> metas = new HashSet<>();
        if (StringUtils.isNotBlank(names)) {
            String[] arr = StringUtils.split(names, ",");
            for (String str : arr) {
                Meta meta = new Meta();
                meta.setName(str);
                meta.setType(type);
                metas.add(meta);
            }
        }
        return metas;
    }

    @Override
    public List<Meta> getMetas(String type) {
        Sort sort = Sort.by(
                Sort.Order.desc("sort"),
                Sort.Order.desc("id"));
        Specification<Meta> specification = (Root<Meta> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) ->
            cb.and(cb.equal(root.get("type").as(String.class), type));
        return metaJPA.findAll(specification, sort);
    }

    @Override
    public List<Meta> getMetaList(String type, Map<String, Sort.Direction> order, int limit) {
        if (StringUtils.isNotBlank(type) && order != null) {
            List<Sort.Order> orderList = new ArrayList<>();
            for (Map.Entry<String, Sort.Direction> entry : order.entrySet()) {
                if (entry.getValue().equals(Sort.Direction.DESC)) {
                    orderList.add(Sort.Order.desc(entry.getKey()));
                } else {
                    orderList.add(Sort.Order.asc(entry.getKey()));
                }
            }
            if (limit < 1 || limit > WebConstant.MAX_POSTS) {
                limit = 10;
            }
            Pageable pageable;
            if (orderList.isEmpty()) {
                pageable = PageRequest.of(0, limit);
            } else {
                pageable = PageRequest.of(0, limit, Sort.by(orderList));
            }

            Specification<Meta> specification = (Root<Meta> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) ->
                cb.and(cb.equal(root.get("type").as(String.class), type));

            return metaJPA.findAll(specification, pageable).getContent();
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void saveMeta(String type, String name, Integer mid) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            Meta find = metaJPA.getByNameAndType(name, type);
            if (null != find) {
                throw new TipException("The meta exists!");
            } else {
                Meta meta = new Meta();
                meta.setName(name);
                meta.setType(type);
                // TODO Category
                if (null != mid) {
                    meta.setId(mid);
                }
                metaJPA.save(meta);
            }
        }
    }

    @Override
    public void saveMetas(Set<Meta> metas) {
        for (Meta meta : metas) {
            Meta find = metaJPA.getByNameAndType(meta.getName(), meta.getType());
            if (null == find) {
                metaJPA.save(meta);
            } else if (meta.getType().equals(Types.CATEGORY.getType())) {
                find.setCount(find.getCount() + 1);
                metaJPA.save(find);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        metaJPA.deleteById(id);
    }

    @Override
    public void saveMeta(Meta meta) {
        metaJPA.save(meta);
    }
}
