package com.itheima.service.mongo.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.itheima.domain.mongo.Friend;
import com.itheima.domain.mongo.RecommendUser;
import com.itheima.domain.mongo.UserLike;
import com.itheima.domain.mongo.Visitor;
import com.itheima.service.mongo.UserLikeService;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.UserLikeCountVo;
import com.itheima.vo.UserLikeVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserLikeServiceImpl implements UserLikeService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long likeUserId) {
        //1. 查询我是否已经喜欢了对方
        Query query = new Query(
                Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId)
        );
        boolean isExists = mongoTemplate.exists(query, UserLike.class);

        //2. 如果没有, 接下来保存
        if (!isExists) {
            //构建userLike
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setLikeUserId(likeUserId);
            userLike.setCreated(System.currentTimeMillis());
            //保存
            mongoTemplate.save(userLike);
        }
    }

    @Override
    public boolean isMutualLike(Long userId, Long likeUserId) {
        //1. 查询我是否已经喜欢了对方
        boolean isExists1 =
                mongoTemplate.exists(new Query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId)), UserLike.class);

        //2. 查询对方是否已经喜欢了我
        boolean isExists2 =
                mongoTemplate.exists(new Query(Criteria.where("userId").is(likeUserId).and("likeUserId").is(userId)), UserLike.class);

        return isExists1 && isExists2;
    }

    @Override
    public void delete(Long userId, Long likeUserId) {
        //删除我喜欢她
        mongoTemplate.remove(new Query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId)), UserLike.class);
    }

    @Override
    public UserLikeCountVo findUserCount(Long userId) {
        //- 相互喜欢: 查询好友列表 (  从friend表中根据userId查询我的所有好友数量  )
        Query query1 = new Query(Criteria.where("userId").is(userId));
        long eachLoveCount = mongoTemplate.count(query1, Friend.class);

        //- 喜欢: 查询你喜欢的用户 (  从user_like表中根据userId查询所有我喜欢的用户数量  )
        Query query2 = new Query(Criteria.where("userId").is(userId));
        long loveCount = mongoTemplate.count(query2, UserLike.class);

        //- 粉丝: 查询别人喜欢你的用户 (  从user_like表中根据likeUserId查询所有喜欢我的用户数量 )
        Query query3 = new Query(Criteria.where("likeUserId").is(userId));
        long fanCount = mongoTemplate.count(query3, UserLike.class);

        //返回对象
        UserLikeCountVo userLikeCountVo = new UserLikeCountVo();
        userLikeCountVo.setEachLoveCount(eachLoveCount);
        userLikeCountVo.setLoveCount(loveCount);
        userLikeCountVo.setFanCount(fanCount);

        return userLikeCountVo;
    }

    @Override
    public PageBeanVo findUserList(Long userId, Integer type, Integer pageNum, Integer pageSize) {
        if (type == 1) {
            //1. 相互喜欢:查询好友列表(从friend表中根据userId分页查询我的好友)
            //1-1 分页查询到friend列表和统计
            Query query = new Query(Criteria.where("userId").is(userId)).skip((pageNum - 1) * pageSize).limit(pageSize);
            List<Friend> friendList = mongoTemplate.find(query, Friend.class);
            long count = mongoTemplate.count(query, Friend.class);

            //1-2 将列表转换成userLikeVoList
            List<UserLikeVo> userLikeVoList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(friendList)) {
                for (Friend friend : friendList) {
                    Long friendId = friend.getFriendId();//目标用户的id

                    UserLikeVo userLikeVo = new UserLikeVo();
                    userLikeVo.setId(friendId);//用户id
                    userLikeVo.setMatchRate(findScore(userId, friendId).intValue());//设置匹配度
                    userLikeVoList.add(userLikeVo);
                }
            }

            //1-3 构建返回分页vo
            return new PageBeanVo(pageNum, pageSize, count, userLikeVoList);
        } else if (type == 2) {
            //- 喜欢:查询你喜欢的用户(从user_like表中根据userId分页查询我喜欢的用户)
            //2-1 分页查询到friend列表和统计
            Query query = new Query(Criteria.where("userId").is(userId)).skip((pageNum - 1) * pageSize).limit(pageSize);
            List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
            long count = mongoTemplate.count(query, UserLike.class);

            //2-2 将列表转换成userLikeVoList
            List<UserLikeVo> userLikeVoList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(userLikeList)) {
                for (UserLike userLike : userLikeList) {
                    Long friendId = userLike.getLikeUserId();//目标用户的id

                    UserLikeVo userLikeVo = new UserLikeVo();
                    userLikeVo.setId(friendId);//用户id
                    userLikeVo.setMatchRate(findScore(userId, friendId).intValue());//设置匹配度
                    userLikeVoList.add(userLikeVo);
                }
            }

            //2-3 构建返回分页vo
            return new PageBeanVo(pageNum, pageSize, count, userLikeVoList);


        } else if (type == 3) {
            //- 粉丝:查询别人喜欢你的用户(从user_like表中根据likeUserId分页查询喜欢我的用户)
            //3-1 分页查询到friend列表和统计
            Query query = new Query(Criteria.where("likeUserId").is(userId)).skip((pageNum - 1) * pageSize).limit(pageSize);
            List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
            long count = mongoTemplate.count(query, UserLike.class);

            //3-2 将列表转换成userLikeVoList
            List<UserLikeVo> userLikeVoList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(userLikeList)) {
                for (UserLike userLike : userLikeList) {
                    Long friendId = userLike.getUserId();//目标用户的id

                    UserLikeVo userLikeVo = new UserLikeVo();
                    userLikeVo.setId(friendId);//用户id
                    userLikeVo.setMatchRate(findScore(userId, friendId).intValue());//设置匹配度
                    //判断是是否已经喜欢了你的粉丝
                    if (isMutualLike(userId, friendId)) {
                        userLikeVo.setAlreadyLove(true);
                    }

                    userLikeVoList.add(userLikeVo);
                }
            }

            //3-3 构建返回分页vo
            return new PageBeanVo(pageNum, pageSize, count, userLikeVoList);

        } else {
            //- 谁看过我:查询访客列表(从visitor表中根据userId分页查询访问过的用户)
            //4-1 分页查询到friend列表和统计
            Query query = new Query(Criteria.where("userId").is(userId)).skip((pageNum - 1) * pageSize).limit(pageSize);
            List<Visitor> visitorList = mongoTemplate.find(query, Visitor.class);
            long count = mongoTemplate.count(query, Visitor.class);

            //4-2 将列表转换成userLikeVoList
            List<UserLikeVo> userLikeVoList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(visitorList)) {
                for (Visitor visitor : visitorList) {
                    Long friendId = visitor.getVisitorUserId();//目标用户的id

                    UserLikeVo userLikeVo = new UserLikeVo();
                    userLikeVo.setId(friendId);//用户id
                    userLikeVo.setMatchRate(findScore(userId, friendId).intValue());//设置匹配度
                    userLikeVoList.add(userLikeVo);
                }
            }

            //4-3 构建返回分页vo
            return new PageBeanVo(pageNum, pageSize, count, userLikeVoList);
        }
    }

    //根据两个用户的id,查询其缘分值
    public Double findScore(Long userId, Long friendId) {
        Query query = new Query(
                Criteria.where("userId").is(friendId).and("toUserId").is(userId)
        );
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        if (recommendUser == null) {
            return RandomUtil.randomDouble(80, 99);
        }
        return recommendUser.getScore();
    }
}
