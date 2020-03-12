package com.wzy.scheduleshare.LoginAndRegister.presenter.impl;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.LoginAndRegister.presenter.inter.LoginPresenter;
import com.wzy.scheduleshare.LoginAndRegister.view.LoginActivity;
import com.wzy.scheduleshare.MainFourPage.view.MainActivity;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * @ClassName LoginPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class LoginPresenterImpl extends BasePresenter<LoginActivity> implements LoginPresenter {

    public LoginPresenterImpl(@NonNull LoginActivity view) {
        super(view);
    }

    private static final int REQUEST_READ_CONTACTS = 0;

    @Override
    public void attemptLogin() {
        if (!BmobUser.isLogin()) {
            return;
        }
        Intent intent = new Intent(mView, MainActivity.class);
        mView.startActivity(intent);
        mView.finish();
        mView.showProgress(false);
    }

    public void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }

    @Override
    public void login(final String username, final String password) {
        User user = new User();
        //此处替换为你的用户名
        user.setUsername(username);
        //此处替换为你的密码
        user.setPassword(password);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User bmobUser, BmobException e) {
                if (e == null) {
                    mView.connectIM();
                    Intent intent=new Intent(mView,MainActivity.class);
                    mView.startActivity(intent);
                    mView.finish();
                } else {
                    mView.showToast(e.getMessage());
                }
                mView.showProgress(false);
            }
        });
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mView.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        mView.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        return false;
    }


}
