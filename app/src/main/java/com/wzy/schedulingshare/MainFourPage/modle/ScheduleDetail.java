package com.wzy.schedulingshare.MainFourPage.modle;

import com.wzy.schedulingshare.base.modle.User;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

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

    private String createTime;   //保存在数据库的主键值
    private String title;  //标题
    private String content; //内容
    private String brief;  //用于在list界面的content的简略信息
    private String startAt;  //事件开始时间
    private String endAT; //事件结束时间
    private User auth;   //作者
    private String status;  //状态码，0:未分享  1::已分享

    private BmobRelation comment_relation;   //评论

    public BmobRelation getComment_relation() {
        return comment_relation;
    }

    public void setComment_relation(BmobRelation comment_relation) {
        this.comment_relation = comment_relation;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

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

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ScheduleDetail) {
            if (((ScheduleDetail) other).getCreateTime().equals(this.createTime)) {
                return true;
            }
        }
        return false;
    }
}
