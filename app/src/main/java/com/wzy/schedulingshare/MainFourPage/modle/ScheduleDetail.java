package com.wzy.schedulingshare.MainFourPage.modle;

import com.wzy.schedulingshare.base.modle.User;

import cn.bmob.v3.BmobObject;

/**
 * @ClassName ScheduleDetail
 * @Author Wei Zhouye
 * @Date 2020/3/8
 * @Version 1.0
 */

/*
* 行程信息类
* */
public class ScheduleDetail extends BmobObject {

    private String title;  //标题
    private String content; //内容
    private String startAt;  //事件开始时间
    private String endAT; //事件结束时间
    private User auth;   //作者
    private String status;  //状态码，备用
    //TODO 评论

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAT() {
        return endAT;
    }

    public void setEndAT(String endAT) {
        this.endAT = endAT;
    }

    public User getAuth() {
        return auth;
    }

    public void setAuth(User auth) {
        this.auth = auth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
