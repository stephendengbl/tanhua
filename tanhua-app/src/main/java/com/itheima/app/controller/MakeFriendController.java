package com.itheima.app.controller;

import com.itheima.app.interceptor.UserHolder;
import com.itheima.app.manager.MakeFriendManager;

import com.itheima.vo.NearUserVo;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.RecommendUserVo;
import com.itheima.vo.VisitorVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MakeFriendController {

    @Autowired
    private MakeFriendManager makeFriendManager;

    //今日佳人
    @GetMapping("/tanhua/todayBest")
    public RecommendUserVo findTodayBest() {
        return makeFriendManager.findTodayBest();
    }


    //推荐用户列表
    @GetMapping("/tanhua/recommendation")
    public PageBeanVo findRecommendUser(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        return makeFriendManager.findRecommendUser(pageNum, pageSize);
    }

    //佳人详情
    @GetMapping("/tanhua/{id}/personalInfo")
    public RecommendUserVo findPersonalInfo(@PathVariable("id") Long recommendUserId) {

        return makeFriendManager.findPersonalInfo(recommendUserId);
    }

    //查询陌生人问题(聊一下)
    @GetMapping("/tanhua/strangerQuestions")
    public String findStrangerQuestion(Long userId) {
        return makeFriendManager.findStrangerQuestion(userId);
    }

    //谁看过我
    @GetMapping("/movements/visitors")
    public List<VisitorVo> findVisitor() {
        return makeFriendManager.findVisitor();
    }


    //上报用户地理位置
    @PostMapping("/baidu/location")
    public void saveUserLocation(@RequestBody Map<String, String> map) {
        //1. 接收参数
        Double longitude = Double.parseDouble(map.get("longitude"));
        Double latitude = Double.parseDouble(map.get("latitude"));
        String address = map.get("addrStr");

        System.out.println("用户开始上报位置" + map);

        //2. 调用manager保存
        makeFriendManager.saveUserLocation(longitude, latitude, address);
    }

    //附近的人
    @GetMapping("/tanhua/search")
    public List<NearUserVo> findNearUserList(String gender, Long distance) {

        return makeFriendManager.findNearUserList(gender, distance);
    }

    //获取环信用户信息
    @GetMapping(value = "/huanxin/user")
    public Map<String, String> huanxinUser() {
        Long username = UserHolder.get().getId();

        Map<String, String> result = new HashMap<>();
        result.put("username", username.toString());
        result.put("password", "123456");

        return result;
    }

    //回复陌生人问题(给佳人打招呼)
    @PostMapping("/tanhua/strangerQuestions")
    public void replyStrangerQuestion(@RequestBody Map<String, String> map) {
        Long strangerId = Long.parseLong(map.get("userId"));//陌生人id
        String reply = map.get("reply");//你的回复

        makeFriendManager.replyStrangerQuestion(strangerId, reply);
    }

    //添加联系人
    @PostMapping("/messages/contacts")
    public void saveContact(@RequestBody Map<String, Long> map) {
        //1. 接收到好友id
        Long friendId = map.get("userId");

        //2. 调用manager好友关系
        makeFriendManager.saveContact(friendId);
    }

    //查询联系人
    @GetMapping("/messages/contacts")
    public PageBeanVo findContact(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        return makeFriendManager.findContact(pageNum, pageSize);
    }


    //探花--卡片展示(推荐用户前20条数据[没有第一条])
    @GetMapping("/tanhua/cards")
    public List<RecommendUserVo> findTanhuaCards() {
        return (List<RecommendUserVo>) makeFriendManager.findRecommendUser(1, 20).getItems();
    }


}
