package com.itheima.service.mongo;
import com.itheima.domain.mongo.*;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.TopicAdd;
import com.itheima.vo.TopicBackVo;
import org.bson.types.ObjectId;

import java.util.List;
public interface ToplcService {
    //查询问卷表
    List<TopicQuestionnaire> findQuestionnaire();
    //查询锁表
    List<ToplcLock> findLock(Long userID);

    //向锁表中添加初始化数据
    void saveLock(List<ToplcLock> toplcLockList);

    //查询用户最新鉴定报告
    String findResult(Long userID, String questionId);
    //查询题目
    List<Topic> findTopicType(String level);

    //查询题目对应选项
    List<TopicDetails> findTopicDetails(ObjectId id);
    //根据题id和选项id到选项表中，查询当前题对应的分数
    Integer findScore(String questionId, String optionId);

    //当前题的难度类型、维度类型用来解决一个维度的最高分总和
    Topic findWdType(String questionId);

    ObjectId saveUserScore(TopicUser topicUser);

    Integer findWD(String type, String tMtype,String questionId);

    //判断是什么类型的题
    Topic findType(String questionId);
    //修改问卷锁
    void updateLock(String difficultToType, Long userId);

    TopicResult findConclusion(Integer num);

    TopicUser report(String id);

    TopicResult findJd(String jdid);

    List<TopicUser> findAroundUser(Integer score);

    TopicUser findByTopicUser(Long userId,String level);





    PageBeanVo findByPage(Integer pageNum, Integer pageSize);


    List<TopicBackVo> findList();

    String save(TopicAdd topicAdd);

    TopicAdd findByQId(String id);

    String deleteById(String id);

    String deleteByIds(List<String> ids);
}
