package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.Friend;
import com.itheima.service.mongo.FriendService;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        //userId--friendId
        Query query1 = new Query(
                Criteria.where("userId").is(userId).and("friendId").is(friendId)
        );
        Friend friend1 = mongoTemplate.findOne(query1, Friend.class);
        if (friend1 == null) {
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }

        //friendId---userId
        Query query2 = new Query(
                Criteria.where("userId").is(friendId).and("friendId").is(userId)
        );
        Friend friend2 = mongoTemplate.findOne(query2, Friend.class);
        if (friend2 == null) {
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
    }

    @Override
    public PageBeanVo findByUserId(Long userId, Integer pageNum, Integer pageSize) {
        Query query = new Query(Criteria.where("userId").is(userId))
                .skip((pageNum - 1) * pageSize).limit(pageSize);

        List<Friend> friendList = mongoTemplate.find(query, Friend.class);

        long count = mongoTemplate.count(query, Friend.class);

        return new PageBeanVo(pageNum, pageSize, count, friendList);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        //删除你-她
        mongoTemplate.remove(new Query(Criteria.where("userId").is(userId).and("friendId").is(friendId)), Friend.class);
        //删除她-你
        mongoTemplate.remove(new Query(Criteria.where("userId").is(friendId).and("friendId").is(userId)), Friend.class);
    }
}
