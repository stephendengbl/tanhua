package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementVo implements Serializable {
    private String id;
    private String title;//标题
    private String description;//描述
    private String createDate;//发布时间
}
