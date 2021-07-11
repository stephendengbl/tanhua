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
@Document(collection = "topic_questionnaire")
public class TopicQuestionnaire implements Serializable {
    private ObjectId id;
    private String name;
    private String cover;
    private String level;
    private Integer star;
}
