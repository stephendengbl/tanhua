package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.FocusUser;
import com.itheima.service.mongo.FocusUserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class FocusUserServiceImpl implements FocusUserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(FocusUser focusUser) {
        mongoTemplate.save(focusUser);
    }

    @Override
    public void delete(FocusUser focusUser) {
        Query query = new Query(
                Criteria.where("userId").is(focusUser.getUserId())
                        .and("focusUserId").is(focusUser.getFocusUserId())
        );
        mongoTemplate.remove(query, FocusUser.class);
    }
}
