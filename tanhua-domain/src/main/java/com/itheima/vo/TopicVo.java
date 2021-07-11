package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicVo implements Serializable {
    private String id;//问卷编号
    private String name;//问卷名称
    private String cover;//封面
    private String level;//级别
    private Integer star;//星别
    private List<?> questions = Collections.emptyList(); //列表
    private Integer isLock; //是否锁住
    private String reportId;//报告id
}
