package com.wzy.scheduleshare.Setting.modle;

import com.wzy.scheduleshare.base.modle.User;

import cn.bmob.v3.BmobObject;

/**
 * @ClassName Advice
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */

/*用户留言*/
public class Advice extends BmobObject{
    private User user;
    private String content;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
