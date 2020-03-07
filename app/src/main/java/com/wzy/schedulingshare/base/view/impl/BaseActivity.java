package com.wzy.schedulingshare.base.view.impl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.event.RefreshNewFriendEvent;
import com.wzy.schedulingshare.base.Utils.BmobUtils.IMMLeaks;
import com.wzy.schedulingshare.base.event.RefreshEvent;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;
import com.wzy.schedulingshare.base.view.inter.IBaseView;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;


/**
 * @ClassName BaseActivity
 * @Author Wei Zhouye
 * @Date 2020/2/22
 * @Version 1.0
 */
public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView {


    protected P mPresenter;

    protected abstract int getLayoutId();

    protected String TAG = getClass().getSimpleName();

    @Override
    public void onError(Throwable throwable) {
        //TODO
    }

    @Override
    public void showLoading() {
        //TODO
    }

    @Override
    public void hideLoading() {
        //TODO
    }

    @Override
    public void showToast(int msgId) {
        //分3行写的目的是避免小米手机前面显示应用名
        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setText(getString(msgId));
        toast.show();
        Logger.i(getString(msgId));
    }

    @Override
    public void showToast(String msg) {
        //分3行写的目的是避免小米手机前面显示应用名
        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
        Logger.i(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        Log.i(getClass().getSimpleName(), "----->onCreate");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "----->onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), "----->onPause");
    }

    @Override
    protected void onDestroy() {
        hideLoading();
        if (mPresenter != null) {
            mPresenter = null;
        }
        super.onDestroy();
        Log.i(getClass().getSimpleName(), "----->onDestroy");
    }


    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     *
     * @param
     * @return void
     * @throws
     * @Title: updateUserInfos
     * @Description: TODO
     */
    public void updateUserInfos() {
    }

    /*
    * 执行连接IM服务器的操作
    */
    public void connectIM() {
        final User user = BmobUser.getCurrentUser(User.class);
        // 连接：登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        // 会话：更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getNickname(), user.getHeadIcon()));
                        EventBus.getDefault().post(new RefreshEvent());
                        EventBus.getDefault().post(new RefreshNewFriendEvent());
                        Logger.i("IM服务器连接成功");
                    } else {
                        showToast(e.getMessage());
                    }
                }
            });
            // 连接：监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    Logger.i(status.getMsg());
                    Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    /*
    * 断开IM服务器连接
    * */
    public void disConnect() {
        BmobIM.getInstance().disConnect();
    }


}
