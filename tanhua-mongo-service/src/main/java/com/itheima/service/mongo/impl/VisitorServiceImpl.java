package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.Visitor;
import com.itheima.service.mongo.VisitorService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Visitor> findVisitorList(Long userId, String lastAccessTime) {
        //1. 构建查询
        Query query = new Query(
                Criteria.where("userId").is(userId)
                        .and("date").gt(Long.parseLong(lastAccessTime))
        ).with(Sort.by(Sort.Order.desc("date")))
                .skip(0).limit(4);

        return mongoTemplate.find(query, Visitor.class);
    }
}
