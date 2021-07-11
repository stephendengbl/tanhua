package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReportVo implements Serializable {
    private String conclusion;//鉴定结果
    private String cover;//鉴定图片
    private List<?> dimensions = Collections.emptyList();//维度
    private List<?> similarYou = Collections.emptyList();//与你相似
}
