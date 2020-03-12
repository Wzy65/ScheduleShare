package com.wzy.scheduleshare.base.presenter.impl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.LoginAndRegister.view.LoginActivity;
import com.wzy.scheduleshare.MainFourPage.view.MainActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.modle.TencentCloud;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.view.SplashActivity;
import com.wzy.scheduleshare.base.presenter.inter.SpashPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @ClassName SplashPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public class SplashPresenterImpl extends BasePresenter<SplashActivity> implements SpashPresenter {
    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;

    private String push;

    public SplashPresenterImpl(@NonNull SplashActivity view) {
        super(view);
    }

    @Override
    public void start() {
        new Runnable() {
            @Override
            public void run() {
                /*获取腾讯云密钥*/
                getKey();
                Message message = Message.obtain();
                if (BmobUser.getCurrentUser(User.class).isLogin()) {
                    mView.connectIM();
                    message.what = GO_HOME;
                } else {
                    message.what = GO_LOGIN;
                }
                mHandler.sendMessage(message);
            }
        }.run();
    }

    private void checkPush() {
        Logger.i("检查push："+push);
        SharedPreferences settings = mView.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        String lastPush = settings.getString("lastPush", null);
        if (TextUtils.isEmpty(lastPush) && !TextUtils.isEmpty(push)) {   //说明没有接受过推送
            editor.putString("lastPush", push);
            editor.commit();
            EventBus.getDefault().postSticky(push);
            Logger.i("发送粘性事件："+push);
        } else if (!TextUtils.isEmpty(push) && !push.equals(lastPush)) {  //一天86400秒;
            editor.putString("lastPush", push);
            editor.commit();
            EventBus.getDefault().postSticky(push);
            Logger.i("发送粘性事件："+push);
        }
    }

    private void getKey() {
        BmobQuery<TencentCloud> bmobQuery = new BmobQuery<TencentCloud>();
        bmobQuery.addWhereEqualTo("user", "WZY");
        bmobQuery.findObjects(new FindListener<TencentCloud>() {
            @Override
            public void done(List<TencentCloud> object, BmobException e) {
                if (e == null) {
                    TencentCloud tc = object.get(0);
                    SharedPreferences settings = mView.getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("user", tc.getUser());
                    editor.putString("appid", tc.getAppid());
                    editor.putString("secretId", tc.getSecretId());
                    editor.putString("secretKey", tc.getSecretKey());
                    push = tc.getPush();
                    Logger.i("返回push"+push);
                    editor.commit();
                    Logger.i("获取腾讯云密钥成功");
                    checkPush();
                } else {
                    mView.showToast(R.string.get_tencentKey_error);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    mView.startActivity(new Intent(mView, MainActivity.class));
                    mView.finish();
                    mView.overridePendingTransition(0, 0);//去掉Activity切换间的动画,避免页面雪花
                    break;
                case GO_LOGIN:
                    mView.startActivity(new Intent(mView, LoginActivity.class));
                    mView.finish();
                    mView.overridePendingTransition(0, 0);//去掉Activity切换间的动画,避免页面雪花
                    break;
            }
        }
    };
}
