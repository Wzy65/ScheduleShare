package com.wzy.scheduleshare.Setting.presenter.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.wzy.scheduleshare.MainFourPage.event.RefreshUserEvent;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.Setting.presenter.inter.BaseSettingPresenter;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.wzy.scheduleshare.Setting.View.SettingActivity.REQUEST_AGE;
import static com.wzy.scheduleshare.Setting.View.SettingActivity.REQUEST_AREA;
import static com.wzy.scheduleshare.Setting.View.SettingActivity.REQUEST_EMAIL;
import static com.wzy.scheduleshare.Setting.View.SettingActivity.REQUEST_NICKNAME;

/**
 * @ClassName BaseSettingPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/2/29
 * @Version 1.0
 */
public class BaseSettingPresenterImpl extends BasePresenter<BaseActivity> implements BaseSettingPresenter {
    public BaseSettingPresenterImpl(@NonNull BaseActivity view) {
        super(view);
    }

    @Override
    public void update(int type, final String str) {
        User user = BmobUser.getCurrentUser(User.class);
        switch (type){
            case REQUEST_NICKNAME:
                user.setNickname(str);
                break;
            case REQUEST_EMAIL:
                user.setEmail(str);
                break;
            case REQUEST_AGE:
                user.setAge(Integer.valueOf(str));
                break;
            case REQUEST_AREA:
                user.setArea(str);
                break;
        }
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.setting_update_success);
                    EventBus.getDefault().post(new RefreshUserEvent());
                    mView.finish();
                } else {
                    mView.showToast(R.string.setting_update_fail);
                    Log.e(mView.getLocalClassName(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void updatePassword(String old, String newpw) {
        BmobUser.updateCurrentUserPassword(old, newpw, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.setting_update_success);
                    mView.finish();
                } else {
                    mView.showToast(R.string.setting_update_fail);
                    Log.e(mView.getLocalClassName(), e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean isValueValid(int type, String str) {
        boolean flag = false;
        switch (type) {
            case REQUEST_NICKNAME:
                flag = str.length() >= 2;
                break;
            case REQUEST_EMAIL:
                flag = str.contains("@");
                break;
            case REQUEST_AGE:
                Pattern pattern = Pattern.compile("[0-9]+.?[0-9]+");
                Matcher isNum = pattern.matcher(str);
                flag = isNum.matches();
                break;
            case REQUEST_AREA:
                flag = str.length() >= 2;
                break;
        }
        return flag;
    }
}
