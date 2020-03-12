package com.wzy.scheduleshare.MainFourPage.modle;

import com.wzy.scheduleshare.base.modle.User;

import cn.bmob.v3.BmobObject;

/**
 * @ClassName Comment
 * @Author Wei Zhouye
 * @Date 2020/3/11
 * @Version 1.0
 */
public class Comment extends BmobObject {
    private User auth;  //发表评论的用户
    private String content;  //评论的内容
/*    private ScheduleDetail detail;  //评论所对应的帖子

    public ScheduleDetail getDetail() {
        return detail;
    }

    public void setDetail(ScheduleDetail detail) {
        this.detail = detail;
    }*/

    public User getAuth() {
        return auth;
    }

    public void setAuth(User auth) {
        this.auth = auth;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
