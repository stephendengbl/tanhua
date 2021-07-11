package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.RecommendUser;
import com.itheima.service.mongo.RecommendUserService;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@Service
public class RecommendUserServiceImpl implements RecommendUserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser findMaxScore(Long userId) {
        //1. 构建查询条件
        Query query = new Query(
                Criteria.where("toUserId").is(userId)
        ).with(Sort.by(Sort.Order.desc("score")))
                .skip(0).limit(1);

        //2. 执行查询
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageBeanVo findRecommendUser(Long userId, Integer pageNum, Integer pageSize) {
        //1. 构建查询条件
        Query query = new Query(
                Criteria.where("toUserId").is(userId)
        ).with(Sort.by(Sort.Order.desc("score")))
                .skip((pageNum - 1) * pageSize + 1).limit(pageSize);

        //2. 执行查询
        List<RecommendUser> recommendUserList = mongoTemplate.find(query, RecommendUser.class);

        //3. 数据记录统计
        long count = mongoTemplate.count(query, RecommendUser.class);

        //4. 返回结果
        return new PageBeanVo(pageNum, pageSize, count, recommendUserList);
    }

    @Override
    public RecommendUser findRecommendUser(Long userId, Long recommendUserId) {
        //userId 登录用户   recommendUserId 推荐用户
        Query query = new Query(
                Criteria.where("userId").is(recommendUserId).and("toUserId").is(userId)
        );

        return mongoTemplate.findOne(query,RecommendUser.class);
    }


}
