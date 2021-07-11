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
@Document(collection = "toplc_lock")
public class ToplcLock implements Serializable {
    private ObjectId id;
    private Long   userId;//用户ID
    private Integer lock;//是否上锁
    private String questionnaireId;//试题ID
}