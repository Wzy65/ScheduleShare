package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import com.wzy.schedulingshare.MainFourPage.modle.NewFriend;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName NewFriendPresenter
 * @Author Wei Zhouye
 * @Date 2020/3/5
 * @Version 1.0
 */
public interface NewFriendPresenter extends IBasePresenter {
    interface View{
        void showProgress(final boolean show);
    }

    void agreeAdd(NewFriend add,SaveListener<Object> listener);//添加到好友表中再发送同意添加好友的消息

    void sendAgreeAddFriendMessage(final NewFriend add, final SaveListener<Object> listener); //发送同意添加好友
}
