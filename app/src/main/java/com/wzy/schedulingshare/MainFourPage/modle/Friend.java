package com.wzy.schedulingshare.MainFourPage.modle;

import com.wzy.schedulingshare.base.modle.User;

import cn.bmob.v3.BmobObject;

/**
 * @ClassName Friend
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */

/*好友管理*/
public class Friend extends BmobObject {
    //用户
    private User user;
    //好友
    private User friendUser;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
