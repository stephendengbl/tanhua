package com.itheima.app.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.*;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.ToplcService;
import com.itheima.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToplcManager {
    @Reference
    private ToplcService toplcService;

    @Reference
    private UserInfoService userInfoService;

    /**
     * 对接受的题目和答案进行处理
     * @param answers
     * @return
     */
    public String submitAnswers(List<Answers> answers) {
        Integer num=0;//用户性格评定总分
        Integer wxnum=0;//当前用户外向维度值
        Integer cxnum=0;//抽象维度值
        Integer lxnum=0;//理性维度值
        Integer pdnum=0;//判断维度值
        Integer wxmax=0;//外向维度最高分/用户外向维度得分比例
        Integer cxmax=0;
        Integer lxmax=0;
        Integer pdmax=0;

        Topic topica=null;
        for (Answers answer : answers) {
            //1.得到题目id和选项id
            String questionId= answer.getQuestionId();
            String optionId = answer.getOptionId();
            //2.根据题id和选项id到选项表中，查询当前题对应的分数
            Integer score=toplcService.findScore(questionId, optionId);
            //3.当前题的难度类型、维度类型用来解决一个维度的最高分总和
            topica = toplcService.findWdType(questionId);
            //拿到对应维度的类型
            String type = topica.getCharacterType();
            //拿到问卷难度类型
            String difficultToType = topica.getDifficultToType();
            //4.对用户的答的题的分数做求和
            num += score;
            //对维度分数进行求和统计
            if (StringUtils.equals(type,"1")) {
                wxnum += score;
                wxmax += WDMax(type, difficultToType,questionId);

            } else if (StringUtils.equals(type,"2")) {
                cxnum += score;
                cxmax += WDMax(type, difficultToType,questionId);
            } else if (StringUtils.equals(type,"3")) {
                lxnum += score;
                lxmax += WDMax(type, difficultToType,questionId);
            } else if (StringUtils.equals(type,"4")) {
                pdnum += score;
                pdmax += WDMax(type, difficultToType,questionId);
            }
        }
        //5.算出各个维度占比
        wxnum = wxnum*100/wxmax;
        cxnum = cxnum*100/cxmax;
        lxnum = lxnum*100/lxmax;
        pdnum = pdnum*100/pdmax;
        //6.如果表中存在该用户的信息就更新，没有就存入用户分数表中

        String id123=null;

        String difficultToType1 = topica.getDifficultToType();//得到题类型

        Long userId = UserHolder.get().getId();
        TopicUser topic=toplcService.findByTopicUser(userId,difficultToType1);
        if (topic == null) {
            TopicUser topicUser = new TopicUser();
            topicUser.setUserId(userId);
            topicUser.setScore(num);
            topicUser.setCx(cxnum+"%");
            topicUser.setLx(lxnum+"%");
            topicUser.setPd(pdnum+"%");
            topicUser.setWx(wxnum+"%");
            TopicResult result=toplcService.findConclusion(num);
            topicUser.setJdid(result.getId().toHexString());
            //查询题目类型
            Topic topic1 = toplcService.findWdType(answers.get(0).getQuestionId());
            String difficultToType = topic1.getDifficultToType();
            topicUser.setLevel(difficultToType);
            ObjectId id=toplcService.saveUserScore(topicUser);

            if (id.toHexString() != null) {
                id123= id.toHexString();
            }
        }
        else{
            TopicUser topicUser = new TopicUser();
            topicUser.setId(topic.getId());
            topicUser.setUserId(userId);
            topicUser.setScore(num);
            topicUser.setCx(cxnum+"%");
            topicUser.setLx(lxnum+"%");
            topicUser.setPd(pdnum+"%");
            topicUser.setWx(wxnum+"%");
            TopicResult result=toplcService.findConclusion(num);
            topicUser.setJdid(result.getId().toHexString());
            //查询题目类型
            Topic topic1 = toplcService.findWdType(answers.get(0).getQuestionId());
            String difficultToType = topic1.getDifficultToType();
            topicUser.setLevel(difficultToType);
            ObjectId id=toplcService.saveUserScore(topicUser);

            if (id.toHexString() != null) {
                id123= id.toHexString();
            }
        }





        //更新锁
        String questionId =answers.get(0) .getQuestionId();
        //查询试卷ID
        //中级问卷
        TopicQuestionnaire middleToplc=null;
        //高级问卷
        TopicQuestionnaire advancedToplc=null;
        //先查询问卷表
        List<TopicQuestionnaire> topicQuestionnaireList = toplcService.findQuestionnaire();
        //得到各级别对应问卷
        if(CollectionUtil.isNotEmpty(topicQuestionnaireList)){
            for (TopicQuestionnaire topicQuestionnaire : topicQuestionnaireList) {
                if(StringUtils.equals("2",topicQuestionnaire.getLevel())){
                    middleToplc=topicQuestionnaire;
                }else if(StringUtils.equals("3",topicQuestionnaire.getLevel())){
                    advancedToplc=topicQuestionnaire;
                }
            }
        }
        //判断是什么类型的题

        Topic type = toplcService.findType(questionId);
        String difficultToType = type.getDifficultToType();

        String lock="";
        if(StringUtils.equals(difficultToType,"1")){
            lock="2";
        }else if(StringUtils.equals(difficultToType,"2")){
            lock="3";
        }

        //修改问卷锁
        toplcService.updateLock(lock,UserHolder.get().getId());


        return id123;
    }

    /**
     * 获取各个维度的最高分和
     * @param type
     * @return
     */
    public  Integer WDMax(String type,String TMtype,String questionId) {
        Integer num=toplcService.findWD(type,TMtype,questionId);//查询（）难度的10道题中，（）维度的题的最高分
        return num;
    }



    public List<TopicVo> findToplc() {
        Long userID = UserHolder.get().getId();

        //初级问卷
        TopicQuestionnaire primaryToplc=null;
        //中级问卷
        TopicQuestionnaire middleToplc=null;
        //高级问卷
        TopicQuestionnaire advancedToplc=null;
        //先查询问卷表
        List<TopicQuestionnaire> topicQuestionnaireList = toplcService.findQuestionnaire();
        //得到各级别对应主键ID
        if(CollectionUtil.isNotEmpty(topicQuestionnaireList)){
            for (TopicQuestionnaire topicQuestionnaire : topicQuestionnaireList) {
                if(StringUtils.equals("1",topicQuestionnaire.getLevel())){
                    primaryToplc=topicQuestionnaire;
                }else if(StringUtils.equals("2",topicQuestionnaire.getLevel())){
                    middleToplc=topicQuestionnaire;
                }else if(StringUtils.equals("3",topicQuestionnaire.getLevel())){
                    advancedToplc=topicQuestionnaire;
                }
            }
        }
        //查询锁表(toplc_lock)
        List<ToplcLock> toplcLocks = toplcService.findLock(userID);
        //向锁表(toplc_lock)中添加数据
        if(CollectionUtil.isEmpty(toplcLocks)){
            List<ToplcLock> toplcLockList=new ArrayList<>();
            ToplcLock toplcLock1=new ToplcLock();
            toplcLock1.setUserId(userID);
            toplcLock1.setLock(1);
            toplcLock1.setQuestionnaireId(middleToplc.getId().toHexString());
            ToplcLock toplcLock2=new ToplcLock();
            toplcLock2.setUserId(userID);
            toplcLock2.setLock(1);
            toplcLock2.setQuestionnaireId(advancedToplc.getId().toHexString());
            toplcLockList.add(toplcLock1);
            toplcLockList.add(toplcLock2);
            toplcService.saveLock(toplcLockList);
        }

        List<TopicVo> topicVoList=new ArrayList<>();

        //初级问卷
        TopicVo primarytopicVo=new TopicVo();
        primarytopicVo.setId(primaryToplc.getId().toHexString());
        primarytopicVo.setName(primaryToplc.getName());
        primarytopicVo.setCover(primaryToplc.getCover());
        primarytopicVo.setLevel("初级");
        primarytopicVo.setStar(primaryToplc.getStar());
        //查询题目
        List<QuestionsVo>questionsVos=new ArrayList<>();
        List<Topic> primarytopictopics = toplcService.findTopicType(primaryToplc.getLevel());
        for (Topic primarytopictopic : primarytopictopics) {
            //查询题目对应的选项
            List<TopicDetails> topicDetails = toplcService.findTopicDetails(primarytopictopic.getId());
            List<OptionsVo>options=new ArrayList<>();
            for (TopicDetails topicDetail : topicDetails) {
                OptionsVo optionsVo=new OptionsVo();
                optionsVo.setId(topicDetail.getId().toHexString());
                optionsVo.setOption(topicDetail.getContent());
                options.add(optionsVo);
            }
            QuestionsVo questionsVo=new QuestionsVo();
            questionsVo.setId(primarytopictopic.getId().toHexString());
            questionsVo.setQuestion(primarytopictopic.getOption());
            questionsVo.setOptions(options);
            questionsVos.add(questionsVo);
        }

        primarytopicVo.setQuestions(questionsVos);
        //初级问卷默认不锁
        primarytopicVo.setIsLock(0);
        //查询初级问卷最新报告
        primarytopicVo.setReportId(toplcService.findResult(userID,"1"));


        //中级问卷
        TopicVo middleTopicVo=new TopicVo();
        middleTopicVo.setId(middleToplc.getId().toHexString());
        middleTopicVo.setName(middleToplc.getName());
        middleTopicVo.setCover(middleToplc.getCover());

        middleTopicVo.setLevel("中级");

        middleTopicVo.setStar(middleToplc.getStar());

        //查询中级题目
        List<QuestionsVo>middlequestionsVos=new ArrayList<>();
        List<Topic> middletopictopics = toplcService.findTopicType("2");
        for (Topic middletopictopic : middletopictopics) {
            //查询中级题目对应的选项
            List<TopicDetails> topicDetails = toplcService.findTopicDetails(middletopictopic.getId());
            List<OptionsVo>options=new ArrayList<>();
            for (TopicDetails topicDetail : topicDetails) {
                OptionsVo optionsVo=new OptionsVo();
                optionsVo.setId(topicDetail.getId().toHexString());
                optionsVo.setOption(topicDetail.getContent());
                options.add(optionsVo);
            }
            QuestionsVo questionsVo=new QuestionsVo();
            questionsVo.setId(middletopictopic.getId().toHexString());
            questionsVo.setQuestion(middletopictopic.getOption());
            questionsVo.setOptions(options);
            middlequestionsVos.add(questionsVo);
        }

        middleTopicVo.setQuestions(middlequestionsVos);
        //中级问卷判断是否加锁
        if(CollectionUtil.isNotEmpty(toplcLocks)){
            for (ToplcLock toplcLock : toplcLocks) {
                if(StringUtils.equals(toplcLock.getQuestionnaireId(),middleToplc.getId().toHexString())){
                    if(toplcLock.getLock()==0){
                        middleTopicVo.setIsLock(0);
                    }else {
                        middleTopicVo.setIsLock(1);
                    }
                }
            }
        }else {
            middleTopicVo.setIsLock(1);
        }
        //查询中级问卷最新报告
        middleTopicVo.setReportId(toplcService.findResult(userID,"2"));




        //高级问卷
        TopicVo advancedTopicVo=new TopicVo();
        advancedTopicVo.setId(advancedToplc.getId().toHexString());
        advancedTopicVo.setName(advancedToplc.getName());
        advancedTopicVo.setCover(advancedToplc.getCover());

        advancedTopicVo.setLevel("高级");

        advancedTopicVo.setStar(advancedToplc.getStar());

        //查询高级题目
        List<QuestionsVo>advancedquestionsVos=new ArrayList<>();
        List<Topic> advancedtopictopics = toplcService.findTopicType("3");
        for (Topic advancedtopictopic : advancedtopictopics) {
            //查询高级题目对应的选项
            List<TopicDetails> topicDetails = toplcService.findTopicDetails(advancedtopictopic.getId());
            List<OptionsVo>options=new ArrayList<>();
            for (TopicDetails topicDetail : topicDetails) {
                OptionsVo optionsVo=new OptionsVo();
                optionsVo.setId(topicDetail.getId().toHexString());
                optionsVo.setOption(topicDetail.getContent());
                options.add(optionsVo);
            }
            QuestionsVo questionsVo=new QuestionsVo();
            questionsVo.setId(advancedtopictopic.getId().toHexString());
            questionsVo.setQuestion(advancedtopictopic.getOption());
            questionsVo.setOptions(options);
            advancedquestionsVos.add(questionsVo);
        }

        advancedTopicVo.setQuestions(advancedquestionsVos);
        //高级问卷判断是否加锁
        if(CollectionUtil.isNotEmpty(toplcLocks)){
            for (ToplcLock toplcLock : toplcLocks) {
                if(StringUtils.equals(toplcLock.getQuestionnaireId(),advancedToplc.getId().toHexString())){
                    if(toplcLock.getLock()==0){
                        advancedTopicVo.setIsLock(0);
                    }else {
                        advancedTopicVo.setIsLock(1);
                    }
                }
            }
        }else {
            advancedTopicVo.setIsLock(1);
        }
        //查询高级问卷最新报告
        advancedTopicVo.setReportId(toplcService.findResult(userID,"3"));


        topicVoList.add(primarytopicVo);
        topicVoList.add(middleTopicVo);
        topicVoList.add(advancedTopicVo);


        return topicVoList;
    }

    public ReportVo report(String id,Long bid) {
        if (id != null) {
            //1.先通过报告id查到题id
            ReportVo reportVo = new ReportVo();
            //1.根据id去报告表中去查询鉴定表的id，然后查询鉴定结果，鉴定图片
            TopicUser topicUser= toplcService.report(id);
            String jdid = topicUser.getJdid();
            TopicResult topicResult= toplcService.findJd(jdid);
            reportVo.setConclusion(topicResult.getConclusion());
            reportVo.setCover(topicResult.getCover());
            //2.从用报告表中查询用户维度的各个得分
            List<DimensionsVo> list = new ArrayList<>();
            DimensionsVo wx = new DimensionsVo("外向",topicUser.getWx().toString());
            DimensionsVo cx = new DimensionsVo("抽象",topicUser.getCx().toString());
            DimensionsVo lx = new DimensionsVo("理性",topicUser.getLx().toString());
            DimensionsVo pd = new DimensionsVo("判断",topicUser.getPd().toString());
            list.add(wx);
            list.add(cx);
            list.add(lx);
            list.add(pd);
            reportVo.setDimensions(list);
            //3.先查总分在上下浮动5之内的
            List<TopicUser> topicUsers=toplcService.findAroundUser(topicUser.getScore());
            //4.如果查询的列表不为空，根据报告表中的userid 到userInfo中查询用户基本信息
            if (CollectionUtil.isNotEmpty(topicUsers)) {
                List<SimilarYouVo> list2 = new ArrayList<>();
                for (TopicUser user : topicUsers) {
                    SimilarYouVo similarYouVo = new SimilarYouVo();
                    Long userId = user.getUserId();
                    if (userId != bid) {
                        UserInfo userInfo = userInfoService.findById(userId);
                        similarYouVo.setId(Integer.parseInt(userId.toString()));
                        similarYouVo.setAvatar(userInfo.getAvatar());
                        list2.add(similarYouVo);
                    }
                }
                reportVo.setSimilarYou(list2);
                return reportVo;
            }
        }
        return null;
    }

}
