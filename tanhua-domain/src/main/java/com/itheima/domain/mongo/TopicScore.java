package com.itheima.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Toplc_score")
public class TopicScore implements Serializable {

    private ObjectId id;//主键ID
    private String   topicId;//题目ID
    private Integer A;
    private Integer B;
    private Integer C;
    private Integer D;
    private Integer E;
    private Integer F;
    private Integer G;
}