package com.itheima.web.controller;

import com.itheima.web.manager.MovementReviewManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovementReviewController {

    @Autowired
    private MovementReviewManager mrm;

    //审核通过
    @PostMapping("/manage/messages/pass")
    public String messageVia(@RequestBody List<String> item) {
        return mrm.messageVia(item);
    }

    //审核拒绝
    @PostMapping("/manage/messages/reject")
    public String messageRefuse(@RequestBody List<String> item) {
        return mrm.messageRefuse(item);
    }
}