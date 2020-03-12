package com.wzy.scheduleshare.base.modle;

import cn.bmob.v3.BmobObject;

/**
 * @ClassName TencentCloud
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
/*用于获取访问腾讯云的密钥*/
public class TencentCloud extends BmobObject{
    private String user;
    private String appid;
    private String secretId;
    private String secretKey;
    private String push;  //并不是真的推送，而是主动获取。跟腾讯云无关，但为了减少一次请求，所以将这个字段放在这里，每次打开APP都主动请求

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAppid() {
        return appid;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getPush() {
        return push;
    }
}
