package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionsVo implements Serializable {
    private String id;//试题Id
    private String question;//试题内容
    List<?> options = Collections.emptyList();
}
