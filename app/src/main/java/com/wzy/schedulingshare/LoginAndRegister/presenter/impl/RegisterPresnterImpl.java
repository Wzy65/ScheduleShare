package com.wzy.schedulingshare.LoginAndRegister.presenter.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.LoginAndRegister.presenter.inter.RegisterPresenter;
import com.wzy.schedulingshare.LoginAndRegister.view.RegisterActivity;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName RegisterPresnterImpl
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class RegisterPresnterImpl extends BasePresenter<RegisterActivity> implements RegisterPresenter {

    public RegisterPresnterImpl(@NonNull RegisterActivity view) {
        super(view);
    }


    @Override
    public boolean isUserIdValid(String userId) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(userId);
        return m.matches();
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @Override
    public boolean isPasswordConfirmValid(String password1, String password2) {
        return password1.equals(password2);
    }

    public void signUp(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setMobilePhoneNumber(username);
        user.setNickname(String.format(mView.getString(R.string.initial_nickname),username));
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.register_success);
                    mView.finish();
                } else {
                    mView.onFailure(e);
                    Log.e(mView.getLocalClassName(),e.getMessage());
                }
                mView.showProgress(false);
            }
        });
    }

}
