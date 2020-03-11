package com.wzy.schedulingshare.base.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.LoginAndRegister.view.LoginActivity;
import com.wzy.schedulingshare.MainFourPage.view.MainActivity;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.modle.TencentCloud;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

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
                /*获取腾讯云密钥*/
                getKey();
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

    private void getKey(){
        BmobQuery<TencentCloud> bmobQuery = new BmobQuery<TencentCloud>();
        bmobQuery.addWhereEqualTo("user","WZY");
        bmobQuery.findObjects(new FindListener<TencentCloud>() {
            @Override
            public void done(List<TencentCloud> object, BmobException e) {
                if (e == null) {
                    TencentCloud tc=object.get(0);
                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("user", tc.getUser());
                    editor.putString("appid", tc.getAppid());
                    editor.putString("secretId", tc.getSecretId());
                    editor.putString("secretKey", tc.getSecretKey());
                    editor.commit();
                    Logger.i("获取腾讯云密钥成功");
                } else {
                    showToast(R.string.get_tencentKey_error);
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
