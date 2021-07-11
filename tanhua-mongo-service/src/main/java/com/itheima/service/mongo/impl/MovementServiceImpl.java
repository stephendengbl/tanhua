package com.itheima.service.mongo.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.itheima.domain.mongo.*;
import com.itheima.service.mongo.MovementService;
import com.itheima.util.ConstantUtil;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovementServiceImpl implements MovementService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    @Override
    public void saveMovement(Movement movement) {
        //0 设置pid
        movement.setPid(idService.getNextId(ConstantUtil.MOVEMENT_ID));

        //1. 先向动态表插入动态信息,获取到动态的id
        mongoTemplate.save(movement);
        ObjectId movementId = movement.getId();

        //2. 向自己的动态表中写入动态的id
        MyMovement myMovement = new MyMovement();
        myMovement.setCreated(System.currentTimeMillis());
        myMovement.setPublishId(movementId);
        mongoTemplate.save(myMovement, ConstantUtil.MOVEMENT_MINE + movement.getUserId());

        //3. 查找到我的所有好友
        Query query = new Query(Criteria.where("userId").is(movement.getUserId()));
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);

        //4. 遍历好友,向好友的朋友表中写入动态id
        if (CollectionUtil.isNotEmpty(friendList)) {
            for (Friend friend : friendList) {
                //得到一个朋友的id
                Long friendId = friend.getFriendId();

                //构建朋友动态表对象
                FriendMovement friendMovement = new FriendMovement();
                friendMovement.setPublishId(movementId);
                friendMovement.setCreated(System.currentTimeMillis());
                friendMovement.setUserId(movement.getUserId());

                //向好友的朋友表中写入动态id
                mongoTemplate.save(friendMovement, ConstantUtil.MOVEMENT_FRIEND + friendId);
            }
        }
    }

    @Override
    public PageBeanVo findMyMovement(Long userId, Integer pageNum, Integer pageSize) {

        //1. 从个人动态表查询到动态id的集合
        Query query = new Query()
                .with(Sort.by(Sort.Order.desc("created")))//排序
                .skip((pageNum - 1) * pageSize).limit(pageSize);//分页

        //2. 遍历动态id的集合
        List<Movement> movementList = new ArrayList<>();

        List<MyMovement> myMovementList = mongoTemplate.find(query, MyMovement.class, ConstantUtil.MOVEMENT_MINE + userId);
        if (CollectionUtil.isNotEmpty(myMovementList)) {
            for (MyMovement myMovement : myMovementList) {
                ObjectId movementId = myMovement.getPublishId();//获取到了动态的id
                //3. 根据动态id从动态详情表中查询详情数据
                Movement movement = mongoTemplate.findById(movementId, Movement.class);
                if (movement.getState() == 1) {
                    movementList.add(movement);
                }
            }
        }

        //4. 统计数量
        long count = mongoTemplate.count(query, MyMovement.class, ConstantUtil.MOVEMENT_MINE + userId);

        //5. 封装返回结果
        return new PageBeanVo(pageNum, pageSize, count, movementList);
    }

    @Override
    public PageBeanVo findMyFriendMovement(Long userId, Integer pageNum, Integer pageSize) {
        //1. 查询我的好友的动态表
        Query query = new Query()
                .with(Sort.by(Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);
        List<FriendMovement> friendMovementList = mongoTemplate.find(query, FriendMovement.class, ConstantUtil.MOVEMENT_FRIEND + userId);

        //2. 将得到的动态id遍历
        List<Movement> movementList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(friendMovementList)) {
            for (FriendMovement friendMovement : friendMovementList) {
                //3. 根据动态id从动态详情表中查询
                ObjectId movementId = friendMovement.getPublishId();
                Movement movement = mongoTemplate.findById(movementId, Movement.class);
                if (movement.getState() == 1) {
                    movementList.add(movement);
                }
            }
        }

        //4. 统计记录数
        long count = mongoTemplate.count(query, FriendMovement.class, ConstantUtil.MOVEMENT_FRIEND + userId);

        //5. 封装返回结果
        return new PageBeanVo(pageNum, pageSize, count, movementList);
    }

    @Override
    public PageBeanVo findRecommendMovement(Long userId, Integer pageNum, Integer pageSize) {
        //1. 从推荐动态表根据被推荐用户id分页查询,得到动态id集合
        Query query = new Query(
                Criteria.where("userId").is(userId)
        ).with(Sort.by(Sort.Order.desc("score"), Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);
        List<RecommendMovement> recommendMovementList = mongoTemplate.find(query, RecommendMovement.class);

        //2. 遍历动态id集合, 根据动态id去动态详情表中查询详情
        List<Movement> movementList = new ArrayList<Movement>();
        if (CollectionUtil.isNotEmpty(recommendMovementList)) {
            for (RecommendMovement recommendMovement : recommendMovementList) {
                //推荐的动态id
                ObjectId movementId = recommendMovement.getPublishId();

                //根据动态id去动态详情表中查询详情
                Movement movement = mongoTemplate.findById(movementId, Movement.class);

                //收集
                if (movement.getState() == 1) {
                    movementList.add(movement);
                }
            }
        }

        //3. 统计总记录数据
        long count = mongoTemplate.count(query, RecommendMovement.class);

        //4. 返回分页vo
        return new PageBeanVo(pageNum, pageSize, count, movementList);
    }

    @Override
    public Movement findMovementById(String movementId) {
        return mongoTemplate.findById(movementId, Movement.class);
    }

    @Override
    public PageBeanVo findMovementList(Long uid, Integer stateInt, Integer pageNum, Integer pageSize) {
        //构建查询条件
        Query query = new Query()
                .with(Sort.by(Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);

        if (uid != null) {
            query.addCriteria(Criteria.where("userId").is(uid));
        }

        if (stateInt != null) {
            query.addCriteria(Criteria.where("state").is(stateInt));
        }

        //查询
        List<Movement> movementList = mongoTemplate.find(query, Movement.class);

        //统计
        long count = mongoTemplate.count(query, Movement.class);

        //返回vo
        return new PageBeanVo(pageNum, pageSize, count, movementList);
    }

    @Override
    public void updateMovementState(Movement movement) {
        mongoTemplate.save(movement);
    }
}
