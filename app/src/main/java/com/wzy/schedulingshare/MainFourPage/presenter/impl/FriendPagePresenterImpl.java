package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.FriendPagePresenter;
import com.wzy.schedulingshare.MainFourPage.view.FriendListFragment;
import com.wzy.schedulingshare.MainFourPage.modle.Friend;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName FriendPagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */
public class FriendPagePresenterImpl extends BasePresenter<FriendListFragment> implements FriendPagePresenter {
    public FriendPagePresenterImpl(@NonNull FriendListFragment view) {
        super(view);
    }


    /*查询好友*/
    @Override
    public void queryFriends() {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects( new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if(e==null) {
                    mView.refreshFriendList(list);
                }
            }
        });
    }

    /*删除好友*/
    @Override
    public void deleteFriend(final Friend f, UpdateListener listener) {
        Friend friend =new Friend();
        //friend.delete(f.getObjectId(),listener);
        friend.delete(f.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Logger.i("删除好友成功");
                } else {
                    Logger.i("删除好友失败");
                }
            }
        });
    }

}
