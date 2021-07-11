package com.itheima.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "topic_user")
public class TopicUser implements Serializable {
    private ObjectId id;
    private Long userId;
    private Integer score;
    private String  wx;//维度外向
    private String  pd;//维度判断
    private String  lx;//维度理性
    private String  cx;//维度抽象
    private String Jdid;//鉴定结果id
    private String level;//题的级别
}
