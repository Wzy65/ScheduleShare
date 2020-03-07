package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import android.view.View;

import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName UserInfoPresenter
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */
public interface UserInfoPresenter extends IBasePresenter {
    interface View {
    }

    void loadUserInfo();  //加载用户信息

    void sendAddFriendMessage(User user, String content);  //发送添加好友请求
}
