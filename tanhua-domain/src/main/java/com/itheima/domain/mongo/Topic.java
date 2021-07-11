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
@Document(collection = "topic")
public class Topic implements Serializable {
    private ObjectId id;
    private String option;//内容
    private String difficultToType;//难度级别 1.初级 2.中级 3.高级
    private String characterType;//1.理性 2.判断 3.外向 4.抽象
}
