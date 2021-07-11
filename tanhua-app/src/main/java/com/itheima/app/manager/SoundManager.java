package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.Sound;
import com.itheima.domain.mongo.SoundTime;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.SoundService;
import com.itheima.service.mongo.SoundTimeServic;
import com.itheima.vo.SoundVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SoundManager {

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private SoundService soundService;

    @Reference
    private SoundTimeServic soundTimeServic;



    //发送语音
    public void sendVoice(MultipartFile soundFile, Long userId) throws IOException {
        //上传到fastDfs
        StorePath storePath = client.uploadFile(soundFile.getInputStream(), soundFile.getSize(), FileUtil.extName(soundFile.getOriginalFilename()), null);
        String soundPath = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        //构建一个对象
        Sound sound = new Sound();
        sound.setSoundUrl(soundPath);
        sound.setCreated(System.currentTimeMillis());
        sound.setUserId(userId);

        //发送人的性别
        UserInfo userInfo = userInfoService.findById(userId);
        sound.setGender(userInfo.getGender());

        //保存到mongo
        soundService.save(sound);
    }


    //接受语音
    public SoundVo receiveVoice(Long userId) {
        //随机查询一个异性语音
        UserInfo userInfo = userInfoService.findById(userId);
        String gender = userInfo.getGender();
        Sound sound = soundService.findByGender(gender);

        //查询当前用户的语音接受次数
        SoundTime soundTime = soundTimeServic.findSoundTime(userId);
        if (soundTime == null) {
            soundTime = new SoundTime();
            soundTime.setSoundTime(10);
            soundTime.setUserId(userId);
        }
        if (soundTime.getSoundTime() > 0) {
            soundTime.setSoundTime(soundTime.getSoundTime()-1);
            soundTimeServic.save(soundTime);
        }

        //封装vo返回
        SoundVo soundVo = new SoundVo();
        BeanUtil.copyProperties(userInfoService.findById(sound.getUserId()), soundVo);
        soundVo.setId(Integer.parseInt(String.valueOf(userInfoService.findById(sound.getUserId()).getId())));
        soundVo.setSoundUrl(sound.getSoundUrl());
        soundVo.setRemainingTimes(soundTime.getSoundTime());
        this.soundService.soundDeleteById(sound.getId());//删除这条语音
        return soundVo;
    }
}
