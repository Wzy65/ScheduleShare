package com.wzy.scheduleshare.MainFourPage.modle;

import com.wzy.scheduleshare.base.modle.User;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @ClassName Backup
 * @Author Wei Zhouye
 * @Date 2020/3/13
 * @Version 1.0
 */
/*用户备份上传Bmob*/
public class Backup extends BmobObject {

    private String createTime;   //保存在数据库的主键值
    private String title;  //标题
    private String content; //内容
    private String brief;  //用于在list界面的content的简略信息
    private String startAt;  //事件开始时间
    private String endAT; //事件结束时间
    private User auth;   //作者
    private String status;  //状态码，0:未分享  1::已分享

    private BmobRelation comment_relation;   //评论

    private String backId;  //保存ScheduleDetail的objectId


    public String getBackId() {
        return backId;
    }

    public void setBackId(String backId) {
        this.backId = backId;
    }

    public User getAuth() {
        return auth;
    }

    public void setAuth(User auth) {
        this.auth = auth;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Backup(ScheduleDetail detail) {
        this.createTime = detail.getCreateTime();   //保存在数据库的主键值
        this.title = detail.getTitle();  //标题
        this.content = detail.getContent(); //内容
        this.brief = detail.getBrief();  //用于在list界面的content的简略信息
        this.startAt = detail.getStartAt();  //事件开始时间
        this.endAT = detail.getEndAT(); //事件结束时间
        this.auth = BmobUser.getCurrentUser(User.class);   //作者
        this.status = detail.getStatus();  //状态码，0:未分享  1::已分享 2:备份数据，不应展示
        this.comment_relation = detail.getComment_relation();   //评论
        this.backId=detail.getObjectId();
        setCreatedAt(detail.getCreatedAt());
        setUpdatedAt(detail.getUpdatedAt());
    }
}
