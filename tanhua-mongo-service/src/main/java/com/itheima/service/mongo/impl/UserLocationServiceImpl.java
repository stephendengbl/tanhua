package com.itheima.service.mongo.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.itheima.domain.mongo.UserLocation;
import com.itheima.service.mongo.UserLocationService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserLocationServiceImpl implements UserLocationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Double longitude, Double latitude, String address) {

        //1. 根据userId查询用户的当前位置
        Query query = new Query(
                Criteria.where("userId").is(userId)
        );

        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);

        if (userLocation == null){
            //2. 如果查不到,新增
            userLocation = new UserLocation();
            userLocation.setUserId(userId);
            userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
            userLocation.setAddress(address);
            userLocation.setCreated(System.currentTimeMillis());
            userLocation.setUpdated(System.currentTimeMillis());
            userLocation.setLastUpdated(System.currentTimeMillis());

            mongoTemplate.save(userLocation);
        }else{
            //3. 如果查到了,更新
            userLocation.setUserId(userId);
            userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
            userLocation.setAddress(address);
            userLocation.setLastUpdated(userLocation.getUpdated());
            userLocation.setUpdated(System.currentTimeMillis());

            mongoTemplate.save(userLocation);
        }
    }

    @Override
    public List<Long> findNearUserIdList(Long userId, Long distance) {
        //1. 查询到我的位置
        Query query1 = new Query(
                Criteria.where("userId").is(userId)
        );
        UserLocation userLocation = mongoTemplate.findOne(query1, UserLocation.class);

        //2. 确定半径
        Distance dis = new Distance(distance / 1000, Metrics.KILOMETERS);

        //3. 化圆
        Circle circle = new Circle(userLocation.getLocation(), dis);

        //4. 搜索圆中的坐标
        Query query2 = new Query(
                Criteria.where("location").withinSphere(circle)
        );
        List<UserLocation> userLocationList = mongoTemplate.find(query2, UserLocation.class);

        //5 获取到圆中的用户id
        List<Long> userIdList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userLocationList)) {
            for (UserLocation location : userLocationList) {
                userIdList.add(location.getUserId());
            }

        }

        return userIdList;
    }
}
