package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.modle.AddFriendMessage;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.UserInfoPresenter;
import com.wzy.schedulingshare.MainFourPage.view.UserInfoActivity;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * @ClassName UserInfoPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */
public class UserInfoPresenterImpl extends BasePresenter<UserInfoActivity> implements UserInfoPresenter {

    public UserInfoPresenterImpl(@NonNull UserInfoActivity view) {
        super(view);
    }

    @Override
    public void loadUserInfo() {

    }

    /*
    * 发送添加好友请求
    * */
    @Override
    public void sendAddFriendMessage(User user, String content) {
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getHeadIcon());
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            mView.showToast(R.string.error_connect_fail);
            Logger.i("尚未连接IM服务器");
            return;
        }
        //会话：创建一个暂态会话入口，发送好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //消息：根据会话入口获取消息管理，发送好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent(content);//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名
        map.put("avatar", currentUser.getHeadIcon());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    mView.showToast(R.string.userinfo_sendAddFriendMsg_seccess);
                    Logger.i("好友请求发送成功，等待验证");
                } else {//发送失败
                    mView.showToast(mView.getString(R.string.userinfo_sendAddFriendMsg_fail) + e.getMessage());
                    Logger.i("发送失败" + e.getMessage());
                }
            }
        });
    }
}
