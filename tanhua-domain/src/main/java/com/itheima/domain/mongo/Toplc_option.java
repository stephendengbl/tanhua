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
@Document(collection = "Toplc_option")
public class Toplc_option implements Serializable {

    private ObjectId id;//主键ID
    private String toplcId;//题目ID
    private String A;
    private String B;
    private String C;
    private String D;
    private String E;
    private String F;
    private String G;
}