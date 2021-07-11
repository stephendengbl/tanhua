package com.itheima.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sound")
public class Sound implements Serializable {
    private ObjectId id;
    //private String sid;//语音id
    private Long userId;//传音人id
    private String soundUrl;//语音地址
    private Long created; //传音时间
    private String gender; //发送语音人的性别
}