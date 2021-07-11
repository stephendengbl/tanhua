package com.itheima.app.controller;

import com.itheima.app.interceptor.UserHolder;
import com.itheima.app.manager.ToplcManager;
import com.itheima.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ToplcController {

    @Autowired
    private ToplcManager toplcManager;
    /**
     * 提交问题和所选项
     * @param answers
     * @return
     */
    @PostMapping("/testSoul")
    public String submitAnswers(@RequestBody AnswersVo answers) {
        List<Answers> list = answers.getAnswers();

        return toplcManager.submitAnswers(list);
    }


    /**
     * 接口一，查询题目
     * @return
     */
    @GetMapping("/testSoul")
    public List<TopicVo> findToplc(){
        return toplcManager.findToplc();
    }

    @GetMapping("/testSoul/report/{id}")
    public ReportVo report(@PathVariable("id") String id) {
        Long userId = UserHolder.get().getId();
        return toplcManager.report(id,userId);
    }

}
