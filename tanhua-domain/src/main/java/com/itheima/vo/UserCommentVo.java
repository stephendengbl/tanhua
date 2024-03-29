package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCommentVo implements Serializable {
    private String id;
    private String avatar;
    private String nickname;
    private String createDate;
}