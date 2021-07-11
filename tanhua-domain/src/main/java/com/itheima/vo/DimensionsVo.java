package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionsVo implements Serializable {//维度
    private String key;//维度项（外向，判断，抽象，理性）
    private String value;//维度值
}
