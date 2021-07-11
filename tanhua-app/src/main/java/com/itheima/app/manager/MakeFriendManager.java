package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.autoconfig.huanxin.HuanXinTemplate;
import com.itheima.domain.db.Question;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.Akustisch;
import com.itheima.domain.mongo.Friend;
import com.itheima.domain.mongo.RecommendUser;
import com.itheima.domain.mongo.Visitor;
import com.itheima.service.db.QuestionService;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.*;
import com.itheima.util.ConstantUtil;
import com.itheima.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MakeFriendManager {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Reference
    private RecommendUserService recommendUserService;

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private QuestionService questionService;

    @Reference
    private VisitorService visitorService;

    @Reference
    private UserLocationService userLocationService;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private FriendService friendService;

    @Autowired
    private FastFileStorageClient client;

    //今日佳人
    public RecommendUserVo findTodayBest() {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service根据登录用户id查询缘分值最高一个用户
        RecommendUser recommendUser = recommendUserService.findMaxScore(userId);

        //3. 如果没有推送用户,将1号用户推送过去
        if (recommendUser == null) {
            //主动构建1号
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);//推荐用户
            recommendUser.setToUserId(userId);//退给谁
            recommendUser.setScore(99D);//缘分值
        }

        // 4. 封装返回vo
        RecommendUserVo recommendUserVo = new RecommendUserVo();
        recommendUserVo.setUserInfo(userInfoService.findById(recommendUser.getUserId()));//推荐用户信息
        recommendUserVo.setFateValue(recommendUser.getScore().longValue());//二人缘分值

        return recommendUserVo;
    }

    //推荐用户列表
    public PageBeanVo findRecommendUser(Integer pageNum, Integer pageSize) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 根据登录用户id调用service分页查询
        PageBeanVo pageBeanVo = recommendUserService.findRecommendUser(userId, pageNum, pageSize);

        //3. 如果没有数据,手动推荐
        List<RecommendUser> recommendUserList = (List<RecommendUser>) pageBeanVo.getItems();
        if (CollectionUtil.isEmpty(recommendUserList)) {
            //手动封装8个用户,放入recommendUserList
            for (Long i = 2L; i < 9L; i++) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(i);//推荐用户
                recommendUser.setToUserId(userId);//退给谁
                recommendUser.setScore(RandomUtil.randomDouble(80, 90));//缘分值

                recommendUserList.add(recommendUser);
            }
        }

        //4. 封装返回结果
        List<RecommendUserVo> recommendUserVoList = new ArrayList<>();
        for (RecommendUser recommendUser : recommendUserList) {
            RecommendUserVo recommendUserVo = new RecommendUserVo();
            recommendUserVo.setUserInfo(userInfoService.findById(recommendUser.getUserId()));//推荐用户信息
            recommendUserVo.setFateValue(recommendUser.getScore().longValue());//二人缘分值
            recommendUserVoList.add(recommendUserVo);
        }

        pageBeanVo.setItems(recommendUserVoList);
        return pageBeanVo;
    }

    //佳人详情
    public RecommendUserVo findPersonalInfo(Long recommendUserId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用recommendUserService查询二人缘分值
        RecommendUser recommendUser = recommendUserService.findRecommendUser(userId, recommendUserId);

        //3. 调用userInfoservice查询佳人信息
        UserInfo userInfo = userInfoService.findById(recommendUserId);

        //4. 封装返回结果
        RecommendUserVo recommendUserVo = new RecommendUserVo();
        recommendUserVo.setUserInfo(userInfo);
        if (recommendUser == null) {
            recommendUserVo.setFateValue(99L);
        } else {
            recommendUserVo.setFateValue(recommendUser.getScore().longValue());
        }
        return recommendUserVo;
    }

    //查询陌生人问题(聊一下)
    public String findStrangerQuestion(Long userId) {
        //1. 调用service查询陌生人问题
        Question question = questionService.findByUserId(userId);

        //2. 如果没有设置,给默认值
        if (question == null) {
            return "你是喜欢天空的广阔,还是喜欢大海的波澜~~~";
        }
        return question.getStrangerQuestion();
    }

    //谁看过我
    public List<VisitorVo> findVisitor() {
        //0. 创建访客集合
        List<VisitorVo> visitorVoList = new ArrayList<>();

        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 从redis中获取到当前用户的上一次登录时间
        String lastAccessTime = stringRedisTemplate.opsForValue().get(ConstantUtil.LAST_ACCESS_TIME + userId);
        //如果查询不到,之间返回空集合
        if (StringUtils.isEmpty(lastAccessTime)) {
            //4. 重置redis中当前用户的上次访问时间=当前时间
            stringRedisTemplate.opsForValue().set(ConstantUtil.LAST_ACCESS_TIME + userId, System.currentTimeMillis() + "");

            return visitorVoList;
        }

        //3. 调用service查询最近访客列表
        List<Visitor> visitorList = visitorService.findVisitorList(userId, lastAccessTime);

        //4. 重置redis中当前用户的上次访问时间=当前时间
        stringRedisTemplate.opsForValue().set(ConstantUtil.LAST_ACCESS_TIME + userId, System.currentTimeMillis() + "");

        //5. 组装返回结果
        if (CollectionUtil.isNotEmpty(visitorList)) {
            for (Visitor visitor : visitorList) {
                VisitorVo visitorVo = new VisitorVo();
                visitorVo.setUserInfo(userInfoService.findById(visitor.getVisitorUserId()));
                visitorVo.setFateValue(visitor.getScore().longValue());
                visitorVoList.add(visitorVo);
            }
        }

        return visitorVoList;
    }

    //上报用户地理位置
    public void saveUserLocation(Double longitude, Double latitude, String address) {
        //1. 获取用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service保存
        userLocationService.save(userId, longitude, latitude, address);
    }

    //附近的人
    public List<NearUserVo> findNearUserList(String gender, Long distance) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service查询附近的人
        List<Long> userIdList = userLocationService.findNearUserIdList(userId, distance);

        //3. 处理返回结果
        List<NearUserVo> nearUserVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userIdList)) {
            for (Long uid : userIdList) {
                //1. 根据uid查询到用户信息
                UserInfo userInfo = userInfoService.findById(uid);

                //2. 排除自己
                if (userId == uid) {
                    continue;
                }

                //3. 排除同性
                if (!StringUtils.equals(userInfo.getGender(), gender)) {
                    continue;
                }

                //4. 组装成返回的vo
                NearUserVo nearUserVo = new NearUserVo();
                nearUserVo.setUserId(uid);
                nearUserVo.setAvatar(userInfo.getAvatar());
                nearUserVo.setNickname(userInfo.getNickname());
                nearUserVoList.add(nearUserVo);
            }
        }

        return nearUserVoList;
    }

    //回复陌生人问题
    public void replyStrangerQuestion(Long strangerId, String reply) {

        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 获取登录用户昵称
        String nickname = userInfoService.findById(userId).getNickname();

        //3. 查询佳人的问题
        Question question = questionService.findByUserId(strangerId);
        String strangerQuestion = "你喜欢去看蔚蓝的大海还是去爬巍峨的高山?";
        if (question != null) {
            strangerQuestion = question.getStrangerQuestion();
        }

        //4. 组装一个json
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId + "");
        map.put("nickname", nickname);
        map.put("strangerQuestion", strangerQuestion);
        map.put("reply", reply);
        String json = JSON.toJSONString(map);

        //5. 调用环信的发送
        huanXinTemplate.sendMsg(strangerId + "", json);
    }

    //交友
    public void saveContact(Long friendId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. mongo中保存好友
        friendService.save(userId,friendId);

        //3. 环信中保存好友
        huanXinTemplate.addContacts(userId,friendId);
    }

    //查询联系人
    public PageBeanVo findContact(Integer pageNum, Integer pageSize) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service根据用户id查询好友
        PageBeanVo pageBeanVo = friendService.findByUserId(userId,pageNum,pageSize);

        //3. 遍历好友id集合,获取每个好友的详情
        List<Friend> friendList = (List<Friend>) pageBeanVo.getItems();
        List<ContactVo> contactVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(friendList)) {
            for (Friend friend : friendList) {
                //好友id
                Long friendId = friend.getFriendId();

                //查询好友详情
                UserInfo userInfo = userInfoService.findById(friendId);

                //返回vo
                ContactVo contactVo = new ContactVo();
                contactVo.setUserInfo(userInfo);//复制大部分
                contactVo.setUserId(friendId+"");

                contactVoList.add(contactVo);
            }

        }

        //4. 封装返回结果
        pageBeanVo.setItems(contactVoList);
        return pageBeanVo;
    }

}