package com.itheima.service.mongo.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.itheima.domain.mongo.*;
import com.itheima.service.mongo.ToplcService;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.TopicAdd;
import com.itheima.vo.TopicBackVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;


@Service
public class ToplcServiceImpl implements ToplcService {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 查询问卷类型表
     * @return
     */
    @Override
    public List<TopicQuestionnaire> findQuestionnaire() {

        Query query=new Query();
        List<TopicQuestionnaire> topicQuestionnaireList = mongoTemplate.find(query, TopicQuestionnaire.class);

        return topicQuestionnaireList;
    }

    /**
     * 查询锁表
     * @param userID
     * @return
     */
    @Override
    public List<ToplcLock> findLock(Long userID) {
        Query query=new Query(Criteria.where("userId").is(userID));
        List<ToplcLock> toplcLocks = mongoTemplate.find(query, ToplcLock.class);
        return toplcLocks;
    }

    /**
     * 添加锁表初始化数据
     * @param toplcLockList
     */
    @Override
    public void saveLock(List<ToplcLock> toplcLockList) {
        if(CollectionUtil.isNotEmpty(toplcLockList)){
            for (ToplcLock toplcLock : toplcLockList) {
                mongoTemplate.insert(toplcLock);
            }
        }
    }

    /**
     * 查询用户最新鉴定报告
     * @param userID
     * @param questionId
     * @return
     */
    @Override
    public String findResult(Long userID, String questionId) {
        Query query=new Query(Criteria.where("userId").is(userID)
                .and("level").is(questionId));
        TopicUser topicResult = mongoTemplate.findOne(query, TopicUser.class);
        if(BeanUtil.isNotEmpty(topicResult)){

            return topicResult.getId().toHexString();
        }
        return null;
    }

    /**
     * 查询题目
     * @param level
     */
    @Override
    public List<Topic> findTopicType(String level) {
        Query query=new Query(Criteria.where("difficultToType").is(level));
        List<Topic> topics = mongoTemplate.find(query, Topic.class);
        return topics;
    }
    /**
     * 查询题目对应选项
     * @param id
     * @return
     */
    @Override
    public List<TopicDetails> findTopicDetails(ObjectId id) {
        Query query=new Query(Criteria.where("questionId").is(id.toHexString()));
        List<TopicDetails> topicDetails = mongoTemplate.find(query, TopicDetails.class);
        return topicDetails;
    }




    //查询当前这个选项的分数
    @Override
    public Integer findScore(String questionId, String optionId) {
        //1.问题id和选项id唯一确定一个分数
        Query query = new Query(Criteria.where("questionId").is(questionId).and("id").is(optionId));
        TopicDetails topicDetails = mongoTemplate.findOne(query, TopicDetails.class);
        Integer score = topicDetails.getScore();
        return score;
    }
    //查询当前题是什么类型
    @Override
    public Topic findWdType(String questionId) {
        Query query = new Query(Criteria.where("id").is(questionId));
        Topic topic = mongoTemplate.findOne(query, Topic.class);
        return topic;
    }

    @Override
    public ObjectId saveUserScore(TopicUser topicUser) {
        TopicUser save = mongoTemplate.save(topicUser);
        return save.getId();
    }

    //查询()难度的题中()维度的题的分数
    @Override
    public Integer findWD(String type, String tMtype,String questionId) {
        //1.通过难度类型和维度类型以及题目id唯一确定一个维度的题目id列表
        Query query = new Query(Criteria.where("difficultToType").is(tMtype).and("characterType").is(type));
        List<Topic> topics = mongoTemplate.find(query, Topic.class);
        Integer num=0;
        //2.遍历题目id列表，得到具体的id，用id做条件去选项表中查询题目id为id的所有选项
        for (Topic topic : topics) {
            String id = topic.getId().toHexString();
            Query query1 = new Query(Criteria.where("questionId").is(id));
            List<TopicDetails> topicDetails = mongoTemplate.find(query1, TopicDetails.class);
            //3.找该题的最高分
            Integer test=0;
            for (TopicDetails topicDetail : topicDetails) {
                if (topicDetail.getScore() > test) {
                    test = topicDetail.getScore();
                }
            }
            //4.对该维度的题的最高值求和
            num += test;
        }
        //5.返回num
        return num;
    }

    @Override
    public Topic findType(String questionId) {
        Query query=new Query(Criteria.where("id").is(new ObjectId(questionId)));
        Topic topic = mongoTemplate.findOne(query, Topic.class);
        return topic;
    }

    /**
     * 修改问卷锁
     * @param difficultToType
     * @param userId
     */
    @Override
    public void updateLock(String difficultToType, Long userId) {
        Query query=new Query(Criteria.where("level").is(difficultToType));
        TopicQuestionnaire topicQuestionnaire = mongoTemplate.findOne(query, TopicQuestionnaire.class);

        if(StringUtils.isNotEmpty(difficultToType)){
            Query query1=new Query(Criteria.where("userId").is(userId)
                    .and("questionnaireId").is(topicQuestionnaire.getId().toHexString()));
            ToplcLock templateOne = mongoTemplate.findOne(query1, ToplcLock.class);
            templateOne.setLock(0);
            mongoTemplate.save(templateOne);
        }
    }

    @Override
    public TopicResult findConclusion(Integer num) {
        if (num < 21 && num > 0) {
            return  mongoTemplate.findById("6094ad517568a20a34da6ba9", TopicResult.class);
        } else if (num >= 21 && num <= 40) {
            return  mongoTemplate.findById("6094ad517568a20a34da6baa", TopicResult.class);
        } else if (num >= 41 && num <= 55) {
            return  mongoTemplate.findById("6094ad517568a20a34da6bab", TopicResult.class);
        }
        else{
            return  mongoTemplate.findById("46094ad517568a20a34da6bac", TopicResult.class);
        }
    }
    @Override
    public TopicUser report(String id) {
        return mongoTemplate.findById(id, TopicUser.class);
    }

    @Override
    public TopicResult findJd(String jdid) {
        return mongoTemplate.findById(jdid, TopicResult.class);
    }

    @Override
    public List<TopicUser> findAroundUser(Integer score) {
        Query query = new Query(Criteria.where("score").gte(score - 5).lte(score + 5));
        return mongoTemplate.find(query, TopicUser.class);
    }

    @Override
    public TopicUser findByTopicUser(Long userId,String level) {
        Query query = new Query(Criteria.where("userId").is(userId)
                .and("level").is(level));
        TopicUser top = mongoTemplate.findOne(query, TopicUser.class);
        return top;
    }



    //邓棒亮
    @Override
    public PageBeanVo findByPage(Integer pageNum, Integer pageSize) {
        Query query=new Query().skip((pageNum-1)*pageSize).limit(pageSize);
        List<Topic> topicList = mongoTemplate.find(query, Topic.class);
        long total = mongoTemplate.count(query, Topic.class);
        List<TopicBackVo> topicBackVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(topicList)) {
            for (Topic topic : topicList) {
                TopicBackVo topicBackVo = new TopicBackVo();
                BeanUtil.copyProperties(topic,topicBackVo);
                TopicQuestionnaire topicLevel = mongoTemplate.findOne(new Query(Criteria.where("level").is(topic.getDifficultToType())), TopicQuestionnaire.class);
                topicBackVo.setLevel(topicLevel.getLevel());
                List<TopicDetails> optionList = mongoTemplate.find(new Query(Criteria.where("questionId").is(topic.getId())), TopicDetails.class);
                Map<String, Integer> map = new HashMap<>();
                if (CollectionUtil.isNotEmpty(optionList)) {
                    for (TopicDetails option : optionList) {
                        map.put(option.getContent(),option.getScore());
                    }
                }
                topicBackVo.setContent(map);
                topicBackVoList.add(topicBackVo);
            }
        }
        return new PageBeanVo(pageNum,pageSize,total,topicBackVoList);
    }

    @Override
    public List<TopicBackVo> findList() {
        Query query=new Query();
        List<Topic> topicList = mongoTemplate.find(query, Topic.class);
        long total = mongoTemplate.count(query, Topic.class);
        List<TopicBackVo> topicBackVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(topicList)) {
            for (Topic topic : topicList) {
                TopicBackVo topicBackVo = new TopicBackVo();
                BeanUtil.copyProperties(topic,topicBackVo);
                topicBackVo.setId(topic.getId().toHexString());
                topicBackVo.setLevel(topic.getDifficultToType());
                /*TopicQuestionnaire topicLevel = mongoTemplate.findOne(new Query(Criteria.where("level").is(topic.getDifficultToType())), TopicQuestionnaire.class);
                topicBackVo.setLevel(topicLevel.getName());*/
                List<TopicDetails> optionList = mongoTemplate.find(new Query(Criteria.where("questionId").is(topic.getId().toHexString())), TopicDetails.class);
                Map<String, Integer> map = new LinkedHashMap<>();
                if (CollectionUtil.isNotEmpty(optionList)) {
                    for (TopicDetails option : optionList) {
                        map.put(option.getContent(),option.getScore());
                    }
                }
                Integer index=map.size();
                if (index < 7) {
                    for (int i = 0; i <7-index; i++) {
                        map.put("空选项"+i,0);
                    }
                }
                topicBackVo.setContent(map);
                topicBackVoList.add(topicBackVo);
            }
        }
        return topicBackVoList;
    }

    @Override
    public String save(TopicAdd topicAdd) {
        Topic topic = new Topic();
        if (topicAdd.getId() != null) {
            //修改
            topic.setId(new ObjectId(topicAdd.getId()));
        }
        topic.setOption(topicAdd.getContent());
        topic.setDifficultToType(topicAdd.getCid());
        topic.setCharacterType(topicAdd.getTid());
        topic = mongoTemplate.save(topic);

        if (topicAdd.getOption7() != null&&topicAdd.getScore7()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);

            TopicDetails topicDetails2 = new TopicDetails();
            topicDetails2.setQuestionId(topic.getId().toHexString());
            topicDetails2.setContent(topicAdd.getOption2());
            topicDetails2.setScore(Integer.valueOf(topicAdd.getScore2()));
            mongoTemplate.save(topicDetails2);

            TopicDetails topicDetails3 = new TopicDetails();
            topicDetails3.setQuestionId(topic.getId().toHexString());
            topicDetails3.setContent(topicAdd.getOption3());
            topicDetails3.setScore(Integer.valueOf(topicAdd.getScore3()));
            mongoTemplate.save(topicDetails3);

            TopicDetails topicDetails4 = new TopicDetails();
            topicDetails4.setQuestionId(topic.getId().toHexString());
            topicDetails4.setContent(topicAdd.getOption4());
            topicDetails4.setScore(Integer.valueOf(topicAdd.getScore4()));
            mongoTemplate.save(topicDetails4);

            TopicDetails topicDetails5 = new TopicDetails();
            topicDetails5.setQuestionId(topic.getId().toHexString());
            topicDetails5.setContent(topicAdd.getOption5());
            topicDetails5.setScore(Integer.valueOf(topicAdd.getScore5()));
            mongoTemplate.save(topicDetails5);

            TopicDetails topicDetails6 = new TopicDetails();
            topicDetails6.setQuestionId(topic.getId().toHexString());
            topicDetails6.setContent(topicAdd.getOption6());
            topicDetails6.setScore(Integer.valueOf(topicAdd.getScore6()));
            mongoTemplate.save(topicDetails6);

            TopicDetails topicDetails7 = new TopicDetails();
            topicDetails7.setQuestionId(topic.getId().toHexString());
            topicDetails7.setContent(topicAdd.getOption7());
            topicDetails7.setScore(Integer.valueOf(topicAdd.getScore7()));
            mongoTemplate.save(topicDetails7);
            return "OK";
        } else if (topicAdd.getOption6() != null&&topicAdd.getScore6()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);

            TopicDetails topicDetails2 = new TopicDetails();
            topicDetails2.setQuestionId(topic.getId().toHexString());
            topicDetails2.setContent(topicAdd.getOption2());
            topicDetails2.setScore(Integer.valueOf(topicAdd.getScore2()));
            mongoTemplate.save(topicDetails2);

            TopicDetails topicDetails3 = new TopicDetails();
            topicDetails3.setQuestionId(topic.getId().toHexString());
            topicDetails3.setContent(topicAdd.getOption3());
            topicDetails3.setScore(Integer.valueOf(topicAdd.getScore3()));
            mongoTemplate.save(topicDetails3);

            TopicDetails topicDetails4 = new TopicDetails();
            topicDetails4.setQuestionId(topic.getId().toHexString());
            topicDetails4.setContent(topicAdd.getOption4());
            topicDetails4.setScore(Integer.valueOf(topicAdd.getScore4()));
            mongoTemplate.save(topicDetails4);

            TopicDetails topicDetails5 = new TopicDetails();
            topicDetails5.setQuestionId(topic.getId().toHexString());
            topicDetails5.setContent(topicAdd.getOption5());
            topicDetails5.setScore(Integer.valueOf(topicAdd.getScore5()));
            mongoTemplate.save(topicDetails5);

            TopicDetails topicDetails6 = new TopicDetails();
            topicDetails6.setQuestionId(topic.getId().toHexString());
            topicDetails6.setContent(topicAdd.getOption6());
            topicDetails6.setScore(Integer.valueOf(topicAdd.getScore6()));
            mongoTemplate.save(topicDetails6);
            return "OK";
        }else if (topicAdd.getOption5() != null&&topicAdd.getScore5()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);

            TopicDetails topicDetails2 = new TopicDetails();
            topicDetails2.setQuestionId(topic.getId().toHexString());
            topicDetails2.setContent(topicAdd.getOption2());
            topicDetails2.setScore(Integer.valueOf(topicAdd.getScore2()));
            mongoTemplate.save(topicDetails2);

            TopicDetails topicDetails3 = new TopicDetails();
            topicDetails3.setQuestionId(topic.getId().toHexString());
            topicDetails3.setContent(topicAdd.getOption3());
            topicDetails3.setScore(Integer.valueOf(topicAdd.getScore3()));
            mongoTemplate.save(topicDetails3);

            TopicDetails topicDetails4 = new TopicDetails();
            topicDetails4.setQuestionId(topic.getId().toHexString());
            topicDetails4.setContent(topicAdd.getOption4());
            topicDetails4.setScore(Integer.valueOf(topicAdd.getScore4()));
            mongoTemplate.save(topicDetails4);

            TopicDetails topicDetails5 = new TopicDetails();
            topicDetails5.setQuestionId(topic.getId().toHexString());
            topicDetails5.setContent(topicAdd.getOption5());
            topicDetails5.setScore(Integer.valueOf(topicAdd.getScore5()));
            mongoTemplate.save(topicDetails5);

            return "OK";
        }else if (topicAdd.getOption4() != null&&topicAdd.getScore4()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);

            TopicDetails topicDetails2 = new TopicDetails();
            topicDetails2.setQuestionId(topic.getId().toHexString());
            topicDetails2.setContent(topicAdd.getOption2());
            topicDetails2.setScore(Integer.valueOf(topicAdd.getScore2()));
            mongoTemplate.save(topicDetails2);

            TopicDetails topicDetails3 = new TopicDetails();
            topicDetails3.setQuestionId(topic.getId().toHexString());
            topicDetails3.setContent(topicAdd.getOption3());
            topicDetails3.setScore(Integer.valueOf(topicAdd.getScore3()));
            mongoTemplate.save(topicDetails3);

            TopicDetails topicDetails4 = new TopicDetails();
            topicDetails4.setQuestionId(topic.getId().toHexString());
            topicDetails4.setContent(topicAdd.getOption4());
            topicDetails4.setScore(Integer.valueOf(topicAdd.getScore4()));
            mongoTemplate.save(topicDetails4);
            return "OK";
        }else if (topicAdd.getOption3() != null&&topicAdd.getScore3()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);

            TopicDetails topicDetails2 = new TopicDetails();
            topicDetails2.setQuestionId(topic.getId().toHexString());
            topicDetails2.setContent(topicAdd.getOption2());
            topicDetails2.setScore(Integer.valueOf(topicAdd.getScore2()));
            mongoTemplate.save(topicDetails2);

            TopicDetails topicDetails3 = new TopicDetails();
            topicDetails3.setQuestionId(topic.getId().toHexString());
            topicDetails3.setContent(topicAdd.getOption3());
            topicDetails3.setScore(Integer.valueOf(topicAdd.getScore3()));
            mongoTemplate.save(topicDetails3);
            return "OK";
        }else if (topicAdd.getOption2() != null&&topicAdd.getScore2()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);

            TopicDetails topicDetails2 = new TopicDetails();
            topicDetails2.setQuestionId(topic.getId().toHexString());
            topicDetails2.setContent(topicAdd.getOption2());
            topicDetails2.setScore(Integer.valueOf(topicAdd.getScore2()));
            mongoTemplate.save(topicDetails2);
            return "OK";
        }else if (topicAdd.getOption1() != null&&topicAdd.getScore1()!=null) {
            TopicDetails topicDetails1 = new TopicDetails();
            topicDetails1.setQuestionId(topic.getId().toHexString());
            topicDetails1.setContent(topicAdd.getOption1());
            topicDetails1.setScore(Integer.valueOf(topicAdd.getScore1()));
            mongoTemplate.save(topicDetails1);
            return "OK";
        }
        return "OK";
    }

    @Override
    public TopicAdd findByQId(String id) {
        Topic topic = mongoTemplate.findById(new ObjectId(id), Topic.class);
        List<TopicDetails> optionList = mongoTemplate.find(new Query(Criteria.where("questionId").is(topic.getId().toHexString())), TopicDetails.class);
        TopicAdd topicAdd = new TopicAdd();
        topicAdd.setId(topic.getId().toHexString());
        topicAdd.setCid(topic.getDifficultToType());
        topicAdd.setTid(topic.getDifficultToType());
        topicAdd.setContent(topic.getOption());
        if (CollectionUtil.isNotEmpty(optionList)) {
            if (optionList.size() == 7) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setOption2(optionList.get(1).getContent());
                topicAdd.setOption3(optionList.get(2).getContent());
                topicAdd.setOption4(optionList.get(3).getContent());
                topicAdd.setOption5(optionList.get(4).getContent());
                topicAdd.setOption6(optionList.get(5).getContent());
                topicAdd.setOption7(optionList.get(6).getContent());
                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));
                topicAdd.setScore2(String.valueOf(optionList.get(1).getScore()));
                topicAdd.setScore3(String.valueOf(optionList.get(2).getScore()));
                topicAdd.setScore4(String.valueOf(optionList.get(3).getScore()));
                topicAdd.setScore5(String.valueOf(optionList.get(4).getScore()));
                topicAdd.setScore6(String.valueOf(optionList.get(5).getScore()));
                topicAdd.setScore7(String.valueOf(optionList.get(6).getScore()));
            } else if (optionList.size() == 6) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setOption2(optionList.get(1).getContent());
                topicAdd.setOption3(optionList.get(2).getContent());
                topicAdd.setOption4(optionList.get(3).getContent());
                topicAdd.setOption5(optionList.get(4).getContent());
                topicAdd.setOption6(optionList.get(5).getContent());
                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));
                topicAdd.setScore2(String.valueOf(optionList.get(1).getScore()));
                topicAdd.setScore3(String.valueOf(optionList.get(2).getScore()));
                topicAdd.setScore4(String.valueOf(optionList.get(3).getScore()));
                topicAdd.setScore5(String.valueOf(optionList.get(4).getScore()));
                topicAdd.setScore6(String.valueOf(optionList.get(5).getScore()));
            } else if (optionList.size() == 5) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setOption2(optionList.get(1).getContent());
                topicAdd.setOption3(optionList.get(2).getContent());
                topicAdd.setOption4(optionList.get(3).getContent());
                topicAdd.setOption5(optionList.get(4).getContent());
                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));
                topicAdd.setScore2(String.valueOf(optionList.get(1).getScore()));
                topicAdd.setScore3(String.valueOf(optionList.get(2).getScore()));
                topicAdd.setScore4(String.valueOf(optionList.get(3).getScore()));
                topicAdd.setScore5(String.valueOf(optionList.get(4).getScore()));
            } else if (optionList.size() == 4) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setOption2(optionList.get(1).getContent());
                topicAdd.setOption3(optionList.get(2).getContent());
                topicAdd.setOption4(optionList.get(3).getContent());
                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));
                topicAdd.setScore2(String.valueOf(optionList.get(1).getScore()));
                topicAdd.setScore3(String.valueOf(optionList.get(2).getScore()));
                topicAdd.setScore4(String.valueOf(optionList.get(3).getScore()));

            } else if (optionList.size() == 3) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setOption2(optionList.get(1).getContent());
                topicAdd.setOption3(optionList.get(2).getContent());

                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));
                topicAdd.setScore2(String.valueOf(optionList.get(1).getScore()));
                topicAdd.setScore3(String.valueOf(optionList.get(2).getScore()));

            } else if (optionList.size() == 2) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setOption2(optionList.get(1).getContent());

                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));
                topicAdd.setScore2(String.valueOf(optionList.get(1).getScore()));

            } else if (optionList.size() == 1) {
                topicAdd.setOption1(optionList.get(0).getContent());
                topicAdd.setScore1(String.valueOf(optionList.get(0).getScore()));

            } else {
                return null;
            }
        }

        return topicAdd;
    }

    @Override
    public String deleteById(String id) {
        Topic topic = new Topic();
        topic.setId(new ObjectId(id));
        mongoTemplate.remove(topic);
        mongoTemplate.remove(new Query(Criteria.where("questionId").is(id)),TopicDetails.class);
        return "OK";
    }

    @Override
    public String deleteByIds(List<String> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            for (String id : ids) {
                Topic topic = new Topic();
                topic.setId(new ObjectId(id));
                mongoTemplate.remove(topic);
                mongoTemplate.remove(new Query(Criteria.where("questionId").is(id)),TopicDetails.class);
            }
        }
        return "OK";
    }

}
