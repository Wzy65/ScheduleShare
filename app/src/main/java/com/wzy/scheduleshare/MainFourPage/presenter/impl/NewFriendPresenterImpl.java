package com.wzy.scheduleshare.MainFourPage.presenter.impl;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.base.Utils.IMBmobUtils.Config;
import com.wzy.scheduleshare.base.Utils.IMBmobUtils.NewFriendManager;
import com.wzy.scheduleshare.MainFourPage.modle.AgreeAddFriendMessage;
import com.wzy.scheduleshare.MainFourPage.modle.Friend;
import com.wzy.scheduleshare.MainFourPage.modle.NewFriend;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.NewFriendPresenter;
import com.wzy.scheduleshare.MainFourPage.view.NewFriendActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;

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
import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName NewFriendPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/5
 * @Version 1.0
 */
public class NewFriendPresenterImpl extends BasePresenter<NewFriendActivity> implements NewFriendPresenter {
    public NewFriendPresenterImpl(@NonNull NewFriendActivity view) {
        super(view);
    }

    /*
    * 好友管理：
    * 添加到好友表中再发送同意添加好友的消息
    * */
    @Override
    public void agreeAdd(final NewFriend add, final SaveListener<Object> listener) {
        User user = new User();
        user.setObjectId(add.getUid());
        agreeAddFriend(user, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    sendAgreeAddFriendMessage(add, listener);
                } else {
                    Logger.e(e.getMessage());
                    listener.done(null, e);
                    mView.showToast(R.string.error_newfriendlist_addFriend_fail);
                }
            }
        });
    }

    /*
    * 好友管理：
    * 发送同意添加好友的消息
    * */
    @Override
    public void sendAgreeAddFriendMessage(final NewFriend add, final SaveListener<Object> listener) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            mView.showToast(mView.getString(R.string.error_newfriendlist_connect_fail));
            Logger.i("尚未连接IM服务器");
            return;
        }
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        // 会话：创建一个暂态会话入口，发送同意好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        // 消息：根据会话入口获取消息管理，发送同意好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        final User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent(mView.getString(R.string.newfriendlist_agree_msg));//这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("msg", currentUser.getUsername() + mView.getString(R.string.newfriendlist_agree_notify));//显示在通知栏上面的内容
        map.put("uid", add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    NewFriendManager.getInstance(mView).updateNewFriend(add, Config.STATUS_VERIFIED);
                    listener.done(msg, e);
                } else {//发送失败
                    Logger.e(e.getMessage());
                    listener.done(msg, e);
                    mView.showToast(e.getMessage());
                }
            }
        });
    }

    //好友管理：添加好友
    private void agreeAddFriend(User friend, SaveListener<String> listener) {
        Friend f = new Friend();
        User user = BmobUser.getCurrentUser(User.class);
        f.setUser(user);
        f.setFriendUser(friend);
        f.save(listener);
    }

}
