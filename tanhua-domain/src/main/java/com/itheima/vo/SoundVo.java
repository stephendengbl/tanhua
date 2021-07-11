package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoundVo implements Serializable {

    private Integer id;
    private String nickname; //昵称
    private String avatar; //用户头像
    private String gender; //性别
    private Integer age; //年龄

    private String soundUrl; //语音地址
    private Integer remainingTimes; //剩余次数
}
