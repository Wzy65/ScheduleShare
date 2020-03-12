package com.wzy.scheduleshare.MainFourPage.modle;

import com.wzy.scheduleshare.base.modle.User;

import cn.bmob.v3.BmobObject;

/**
 * @ClassName Collect
 * @Author Wei Zhouye
 * @Date 2020/3/13
 * @Version 1.0
 */
/*保存用户收藏的行程*/
public class Collect extends BmobObject {
    private User user;
    private ScheduleDetail schedule;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ScheduleDetail getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDetail schedule) {
        this.schedule = schedule;
    }
}
