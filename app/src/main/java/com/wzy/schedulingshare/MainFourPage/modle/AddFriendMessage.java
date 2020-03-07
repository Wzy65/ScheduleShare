package com.wzy.schedulingshare.MainFourPage.modle;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.base.Utils.IMBmobUtils.Config;

import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * @ClassName AddFriendMessage
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */

/*
* 好友管理
* 自定义添加好友的消息类型
* */
public class AddFriendMessage extends BmobIMExtraMessage {
    public static final String ADD="add";

    public AddFriendMessage(){}

    @Override
    public String getMsgType() {
        //自定义一个`add`的消息类型
        return "add";
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        return true;
    }

    /**
     * 将BmobIMMessage转成NewFriend
     *
     * @param msg 消息
     * @return
     */
    public static NewFriend convert(BmobIMMessage msg) {
        NewFriend add = new NewFriend();
        String content = msg.getContent();
        add.setMsg(content);
        add.setTime(msg.getCreateTime());
        add.setStatus(Config.STATUS_VERIFY_NONE);
        try {
            String extra = msg.getExtra();
            if (!TextUtils.isEmpty(extra)) {
                JSONObject json = new JSONObject(extra);
                String name = json.getString("name");
                add.setName(name);
                String avatar = json.getString("avatar");
                add.setAvatar(avatar);
                add.setUid(json.getString("uid"));
            } else {
                Logger.i("AddFriendMessage的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add;
    }
}
