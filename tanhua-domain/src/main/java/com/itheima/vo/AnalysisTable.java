package com.itheima.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTable implements Serializable {

    private String title; //日期

    private Integer amount; //数量

}
