package com.wzy.schedulingshare.base.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import com.wzy.schedulingshare.LoginAndRegister.view.LoginActivity;
import com.wzy.schedulingshare.MainFourPage.view.MainActivity;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * @ClassName SplashActivity
 * @Author Wei Zhouye
 * @Date 2020/2/29
 * @Version 1.0
 */
public class SplashActivity extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = GO_HOME;
                if (BmobUser.getCurrentUser(User.class).isLogin()) {
                    connectIM();
                    message.what = GO_HOME;
                } else {
                    message.what = GO_LOGIN;
                }
                mHandler.sendMessage(message);
            }
        }.run();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);//去掉Activity切换间的动画,避免页面雪花
                    break;
                case GO_LOGIN:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    overridePendingTransition(0, 0);//去掉Activity切换间的动画,避免页面雪花
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
