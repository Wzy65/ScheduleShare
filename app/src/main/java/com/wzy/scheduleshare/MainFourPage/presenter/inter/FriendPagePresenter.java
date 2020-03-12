package com.wzy.scheduleshare.MainFourPage.presenter.inter;

import com.wzy.scheduleshare.MainFourPage.modle.Friend;
import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName FriendPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */
public interface FriendPagePresenter extends IBasePresenter {
    interface View {
        void refreshFriendList(List<Friend> list);
    }

    void queryFriends();  //查询好友

    void deleteFriend(Friend f, UpdateListener listener);  //删除好友

}
