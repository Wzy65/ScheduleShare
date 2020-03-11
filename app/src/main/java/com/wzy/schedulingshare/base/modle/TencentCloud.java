package com.wzy.schedulingshare.base.modle;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
