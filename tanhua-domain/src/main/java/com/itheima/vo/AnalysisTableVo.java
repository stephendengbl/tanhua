package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTableVo implements Serializable {

    private List<?> thisYear; //今年

    private List<?> lastYear; //去年

}
