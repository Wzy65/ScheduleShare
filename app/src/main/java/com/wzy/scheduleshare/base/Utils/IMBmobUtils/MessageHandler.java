package com.wzy.scheduleshare.base.Utils.IMBmobUtils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.event.RefreshFriendListEvent;
import com.wzy.scheduleshare.MainFourPage.event.RefreshNewFriendEvent;
import com.wzy.scheduleshare.MainFourPage.modle.AddFriendMessage;
import com.wzy.scheduleshare.MainFourPage.modle.AgreeAddFriendMessage;
import com.wzy.scheduleshare.MainFourPage.modle.Friend;
import com.wzy.scheduleshare.MainFourPage.modle.NewFriend;
import com.wzy.scheduleshare.MainFourPage.view.MainActivity;
import com.wzy.scheduleshare.MainFourPage.view.NewFriendActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.modle.User;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName MessageHandler
 * @Author Wei Zhouye
 * @Date 2020/3/1
 * @Version 1.0
 */
// 集成：自定义消息接收器处理在线消息和离线消息
public class MessageHandler extends BmobIMMessageHandler {

    private Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    /*
    * 在线消息
    * */
    @Override
    public void onMessageReceive(final MessageEvent event) {
        //当接收到服务器发来的消息时，此方法被调用
        executeMessage(event);
        Logger.i("收到在线消息" + event.getMessage().getContent());
    }

    /*
    * //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
    * */
    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
        Map<String, List<MessageEvent>> map = event.getEventMap();
        Logger.i("有" + map.size() + "个用户发来离线消息");
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            Logger.i("用户" + entry.getKey() + "发来" + size + "条消息");
            for (int i = 0; i < size; i++) {
                //处理每条消息
                executeMessage(list.get(i));
            }
        }
    }

    /**
     * 处理消息
     *
     * @param event
     */
    private void executeMessage(final MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        Logger.i(msg.toString());
        Logger.i(msg.getExtra());
        if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {
            //自定义消息类型：0
            processCustomMessage(msg, event.getFromUserInfo());
            Logger.i("自定义消息");
        } else {
            //SDK内部内部支持的消息类型
            processSDKMessage(msg, event);
            Logger.i("非自定义消息" + msg.getMsgType());
        }
    }

    /**
     * 处理SDK支持的消息
     *
     * @param msg
     * @param event
     */
    private void processSDKMessage(BmobIMMessage msg, MessageEvent event) {
        if (BmobNotificationManager.getInstance(context).isShowNotification()) {
            //如果需要显示通知栏，SDK提供以下两种显示方式：
            Intent pendingIntent = new Intent(context, MainActivity.class);
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // 消息接收：多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
            //BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);
            // 消息接收：自定义通知消息：始终只有一条通知，新消息覆盖旧消息
            BmobIMUserInfo info = event.getFromUserInfo();
            //这里可以是应用图标，也可以将聊天头像转成bitmap
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_schedulingshares);
            BmobNotificationManager.getInstance(context).showNotification(largeIcon,
                    info.getName(), msg.getContent(), context.getString(R.string.notify_one_newMsg), pendingIntent);
        } else {
            //直接发送消息事件
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 处理自定义消息类型
     *
     * @param msg
     */
    // 好友管理：接收并处理好友相关的请求
    private void processCustomMessage(BmobIMMessage msg, BmobIMUserInfo info) {
        //消息类型
        String type = msg.getMsgType();
        //处理消息
        if (type.equals(AddFriendMessage.ADD)) {//接收到的添加好友的请求
            NewFriend friend = AddFriendMessage.convert(msg);
            //本地好友请求表做下校验，本地没有的才允许显示通知栏--有可能离线消息会有些重复
            long id = NewFriendManager.getInstance(context).insertOrUpdateNewFriend(friend);
            if (id > 0) {
                showAddNotify(friend);
                //发送页面刷新的广播
                EventBus.getDefault().post(new RefreshNewFriendEvent());
            }
        } else if (type.equals(AgreeAddFriendMessage.AGREE)) {//接收到的对方同意添加自己为好友,此时需要做的事情：1、添加对方为好友，2、显示通知
            AgreeAddFriendMessage agree = AgreeAddFriendMessage.convert(msg);
            addFriend(agree.getFromId());//添加消息的发送方为好友
            showAgreeNotify(info, agree);
            //发送页面刷新的广播
            EventBus.getDefault().post(new RefreshFriendListEvent());
        } else {
            //Toast.makeText(context, "接收到的自定义消息：" + msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示对方添加自己为好友的通知
     *
     * @param friend
     */
    private void showAddNotify(NewFriend friend) {
        Intent pendingIntent = new Intent(context, NewFriendActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //这里可以是应用图标，也可以将聊天头像转成bitmap
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_schedulingshares);
        BmobNotificationManager.getInstance(context).showNotification(largeIcon,
                friend.getName(), friend.getMsg(), friend.getName() + context.getString(R.string.notify_request_addYou), pendingIntent);
    }

    /**
     * 显示对方同意添加自己为好友的通知
     *
     * @param info
     * @param agree
     */
    private void showAgreeNotify(BmobIMUserInfo info, AgreeAddFriendMessage agree) {
        Intent pendingIntent = new Intent(context, MainActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_schedulingshares);
        BmobNotificationManager.getInstance(context).showNotification(largeIcon, info.getName(), agree.getMsg(), agree.getMsg(), pendingIntent);
    }

    /*
    * 设置悬挂式通知(Bmob原本就实现了，这个就当学习了)
    * */
    private void showHangNotify(Class<?> cls, String title, String msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
            Intent intent = new Intent(context, cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent hangIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(hangIntent);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_schedulingshares));
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setContentText(msg);
            builder.setFullScreenIntent(hangIntent, true);

            android.app.Notification notification = builder.build();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("newFriend", "好友通知", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, notification);
        }
    }

    /**
     * 好友管理：收到同意添加好友后添加好友
     *
     * @param uid
     */
    private void addFriend(String uid) {
        User user = new User();
        user.setObjectId(uid);
        agreeAddFriend(user, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Logger.e("添加好友成功");
                } else {
                    Logger.e(e.getMessage());
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