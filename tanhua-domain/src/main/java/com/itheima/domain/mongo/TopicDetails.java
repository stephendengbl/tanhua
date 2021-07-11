package com.itheima.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetails implements Serializable {
    private ObjectId id;
    private String questionId;//题目id
    private Integer score;//分数
    private String content;//内容
}
