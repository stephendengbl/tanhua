package com.itheima.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement implements Serializable {
    private Long id;
    private String title;//标题
    private String description;//描述
    private Date created;//发布时间
    private Date updated;//修改时间

}