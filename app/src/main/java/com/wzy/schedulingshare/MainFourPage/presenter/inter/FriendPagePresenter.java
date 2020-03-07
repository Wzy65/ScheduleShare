package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import com.wzy.schedulingshare.MainFourPage.modle.Friend;
import com.wzy.schedulingshare.MainFourPage.modle.NewFriend;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import java.util.List;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
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
