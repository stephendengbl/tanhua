package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicBackVo implements Serializable {
    private String id;
    private String level;
    private String characterType;
    private String option;//题目
    private Map<String,Integer> content;//选项:分值
}
