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
@Document(collection = "soundTime")
public class SoundTime implements Serializable {
    private ObjectId id;
    private Long userId;
    private Integer soundTime; //想法：后台设置一个定时任务每隔一天，表中所有数据的次数刷新为8
}
