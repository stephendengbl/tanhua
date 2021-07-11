package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

// pid字段自增服务
@Component
public class IdService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Long getNextId(String collName) {
        //查询条件
        Query query = new Query(Criteria.where("collName").is(collName));

        //更新条件
        Update update = new Update();
        update.inc("seqId", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true); // 如果记录不存在，创建
        options.returnNew(true); // 返回新增后的值

        Sequence sequence = mongoTemplate.findAndModify(query, update, options, Sequence.class);
        return sequence.getSeqId();
    }
}