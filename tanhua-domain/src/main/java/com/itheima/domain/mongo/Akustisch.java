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
@Document(collection = "akustisch")
public class Akustisch implements Serializable {
    private ObjectId id;//主键id
    private Long userId;//用户id
    private String soundUrl;//语音地址
    private Integer remainingTimes=10;//剩余次数(默认每天只能接收10次)
    private Boolean state=false;//状态(false为未被接收,true为已被接收)
}