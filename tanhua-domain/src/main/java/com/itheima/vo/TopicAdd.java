package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicAdd implements Serializable {
    private String id;
    private String cid;
    private String tid;
    private String content;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    private String option6;
    private String option7;
    private String score1;
    private String score2;
    private String score3;
    private String score4;
    private String score5;
    private String score6;
    private String score7;
}
