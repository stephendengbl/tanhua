package com.itheima.app.controller;

import com.itheima.app.interceptor.UserHolder;
import com.itheima.app.manager.SoundManager;
import com.itheima.vo.SoundVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class SoundController {
    @Autowired
    private SoundManager soundManager;


    //发送语音
    @PostMapping("/peachblossom")
    public void sendVoice(MultipartFile soundFile) throws IOException {
        Long userId = UserHolder.get().getId();
        soundManager.sendVoice(soundFile,userId);
    }

    //接收语音
    @GetMapping("/peachblossom")
    public SoundVo receiveVoice(){
        return soundManager.receiveVoice(UserHolder.get().getId());
    }
}
