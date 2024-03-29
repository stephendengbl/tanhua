package com.itheima.autoconfig.huawei;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class HuaWeiUGCTemplate {

    private HuaWeiUGCProperties huaWeiUGCProperties;

    public HuaWeiUGCTemplate(HuaWeiUGCProperties huaWeiUGCProperties) {
        this.huaWeiUGCProperties = huaWeiUGCProperties;
    }

    //文本内容审核
    public boolean textContentCheck(String textModeration) {

        String url = "https://moderation.cn-north-1.myhuaweicloud.com/v1.0/moderation/text";
        String reqBody = JSONUtil.createObj()
                .set("categories", StrUtil.split(huaWeiUGCProperties.getCagegoriesText(), ','))
                .set("items", JSONUtil.createArray()
                        .set(JSONUtil.createObj()
                                .set("text", textModeration)
                                .set("type", "content")
                        )
                ).toString();

        String resBody = HttpRequest.post(url)
                .header("X-Auth-Token", this.getToken())
                .contentType("application/json;charset=utf8")
                .setConnectionTimeout(5000)
                .setReadTimeout(5000)
                .body(reqBody)
                .execute()
                .body();

        JSONObject jsonObject = JSONUtil.parseObj(resBody);

        if (jsonObject.containsKey("result") && jsonObject.getJSONObject("result").containsKey("suggestion")) {
            String suggestion = jsonObject.getJSONObject("result").getStr("suggestion").toUpperCase();
            if("PASS".equals(suggestion)) {
                return true;
            }
        }

        return false;
    }

    //图像内容审核
    public boolean imageContentCheck(String[] urls) {

        String url = "https://moderation.cn-north-1.myhuaweicloud.com/v1.0/moderation/image/batch";

        String reqBody = JSONUtil.createObj()
                .set("categories", huaWeiUGCProperties.getCagegoriesImage().split(","))
                .set("urls", urls)
                .toString();


        String resBody = HttpRequest.post(url)
                .header("X-Auth-Token", this.getToken())
                .contentType("application/json;charset=utf8")
                .setConnectionTimeout(5000)
                .setReadTimeout(5000)
                .body(reqBody)
                .execute()
                .body();

        System.out.println("resBody="+resBody);

        JSONObject jsonObject = JSONUtil.parseObj(resBody);

        if(jsonObject.containsKey("result")){
            //审核结果中如果出现一个block或review，整体结果就是不通过，如果全部为PASS就是通过
            if(StrUtil.contains(resBody, "\"suggestion\":\"block\"")){
                return false;
            }else if(StrUtil.contains(resBody, "\"suggestion\":\"review\"")){
                return false;
            }else{
                return true;
            }
        }

        //默认人工审核
        return false;
    }


    //获取token
    private String token;
    private long expire = 0L;
    public String getToken() {

        Long now = System.currentTimeMillis();

        if(now > expire) {
            String url = "https://iam.myhuaweicloud.com/v3/auth/tokens";

            String reqBody = JSONUtil.createObj()
                    .set("auth", JSONUtil.createObj()
                        .set("identity", JSONUtil.createObj()
                                .set("methods", JSONUtil.createArray().set("password"))
                                .set("password", JSONUtil.createObj()
                                        .set("user", JSONUtil.createObj()
                                                .set("domain", JSONUtil.createObj().set("name", huaWeiUGCProperties.getDomain()))
                                                .set("name", huaWeiUGCProperties.getUsername())
                                                .set("password", huaWeiUGCProperties.getPassword())
                                        )
                                )
                        )
                        .set("scope", JSONUtil.createObj()
                                .set("project", JSONUtil.createObj()
                                        .set("name", huaWeiUGCProperties.getProject())
                                )
                        )
            ).toString();

            token = HttpRequest.post(url)
                    .contentType("application/json;charset=utf8")
                    .setConnectionTimeout(2000)
                    .setReadTimeout(3000)
                    .body(reqBody)
                    .execute()
                    .header("X-Subject-Token");

            expire = System.currentTimeMillis() + 23 * 60 * 60 * 1000;
        }
        return token;
    }

}