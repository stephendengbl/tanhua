package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarYouVo implements Serializable {//与你相似
    private Integer id;//用户编号
    private String avatar;//头像
}
