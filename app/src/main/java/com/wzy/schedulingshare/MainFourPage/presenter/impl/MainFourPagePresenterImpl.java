package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.modle.Friend;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.MainFourPagePresenter;
import com.wzy.schedulingshare.MainFourPage.view.MainActivity;
import com.wzy.schedulingshare.MainFourPage.view.UserInfoActivity;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @ClassName MainFourPagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class MainFourPagePresenterImpl extends BasePresenter<MainActivity> implements MainFourPagePresenter {
    public MainFourPagePresenterImpl(@NonNull MainActivity view) {
        super(view);
    }

    @Override
    public boolean searchNewFriend(String phoneNumber) {
        if (!isPhoneNumberValid(phoneNumber)) {
            mView.showToast(R.string.error_mistake_phoneNumber);
            return false;
        }
        if(phoneNumber.equals(BmobUser.getCurrentUser(User.class).getMobilePhoneNumber())){
            mView.showToast(R.string.error_mistake_isUser);
            return false;
        }
        mView.showProgress(true);
        queryUser(phoneNumber);
        return true;
    }

    @Override
    public String getLocalHeadIcon() {
        SharedPreferences settings = mView.getSharedPreferences("UserInfo", 0);
        return settings.getString("LocalHeadIcon", null);
    }

    @Override
    public void clearTCKey() {
        SharedPreferences settings = mView.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user", "");
        editor.putString("appid", "");
        editor.putString("secretId", "");
        editor.putString("secretKey", "");
        editor.commit();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 查询用户表
     */
    private void queryUser(final String phoneNumber) {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("mobilePhoneNumber", phoneNumber);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (object == null || object.size() == 0) {
                    mView.showToast(R.string.error_non_exist_user);
                } else {
                    if (e == null) {
                        checkAddAlready(object.get(0));//mobilePhoneNumber为唯一键，所以只需要获取第一个
                    } else {
                        mView.showToast("查询失败" + e.getMessage());
                        Logger.i("查询失败" + e.getMessage());
                    }
                }
                mView.showProgress(false);
            }
        });
    }

    private void checkAddAlready(final User user){
        BmobQuery<Friend> bmobQuery = new BmobQuery<>();
        BmobQuery<Friend> bq1=new BmobQuery<>();
        bq1.addWhereEqualTo("user", user);
        BmobQuery<Friend> bq2=new BmobQuery<>();
        bq2.addWhereEqualTo("friendUser", BmobUser.getCurrentUser(User.class));
        List<BmobQuery<Friend>> list=new ArrayList<>();
        list.add(bq1);
        list.add(bq2);
        bmobQuery.and(list);  //复合查询
        bmobQuery.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> object, BmobException e) {
                boolean flag=false;
                if(object==null || object.size()==0){
                    flag=e==null;   //查询成功后，从Friend表找不到关联，说明该用户不是本地用户的好友，新页面展示添加按钮。
                }
                Logger.i("查找到"+object.size()+"个好友"+flag);
                Intent intent=new Intent(mView, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.UserInfoKey,user);
                intent.putExtra(UserInfoActivity.ShowAddFriendKey,flag);
                mView.startActivity(intent);
                if(e!=null) {
                    Logger.i(e.getMessage());
                }
            }
        });
    }
}
