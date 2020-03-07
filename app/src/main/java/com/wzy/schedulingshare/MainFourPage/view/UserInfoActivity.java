package com.wzy.schedulingshare.MainFourPage.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.UserInfoPresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.UserInfoPresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * @ClassName UserInfoActivity
 * @Author Wei Zhouye
 * @Date 2020/3/4
 * @Version 1.0
 */
public class UserInfoActivity extends BaseActivity<UserInfoPresenter> implements UserInfoPresenter.View {

    public static final String UserInfoKey = "UserInfoKey";
    public static final String ShowAddFriendKey="ShowAddFriendKey";

    private User mUser;

    @BindView(R.id.userinfo_enter_personalPage)
    LinearLayout mUserinfoEnterPersonalPage;
    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.userinfo_headIcon)
    ImageView mUserinfoHeadIcon;
    @BindView(R.id.userinfo_nickname)
    TextView mUserinfoNickname;
    @BindView(R.id.userinfo_phonenumber)
    TextView mUserinfoPhonenumber;
    @BindView(R.id.userinfo_addFriend)
    LinearLayout mUserinfoAddFriend;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo;
    }

    @Override
    public void initView() {
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标

        mPresenter = new UserInfoPresenterImpl(this);

        mUser = (User) getIntent().getSerializableExtra(UserInfoKey);
        Glide.with(this).
                load(mUser.getHeadIcon()).
                error(R.drawable.ic_picture_error). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())). //显示圆形
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(mUserinfoHeadIcon);
        mUserinfoNickname.setText(String.format(getString(R.string.userinfo_nickname), mUser.getNickname()));
        mUserinfoPhonenumber.setText(String.format(getString(R.string.userinfo_phonenumber), mUser.getMobilePhoneNumber()));

        showAddFriend(getIntent().getBooleanExtra(ShowAddFriendKey,false));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void showDialog2addFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setText(String.format(getString(R.string.dialog_sendAddFriendMsg_hint), BmobUser.getCurrentUser(User.class).getNickname()));
        editText.setMinLines(3);
        builder.setView(editText, 20, 10, 20, 10);
        builder.setTitle(getString(R.string.dialog_sendAddFriendMsg_title));
        builder.setPositiveButton(R.string.dialog_add_friend_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.sendAddFriendMessage(mUser, editText.getText().toString());
            }
        });
        builder.setNeutralButton(R.string.dialog_add_friend_left_btn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void showAddFriend(boolean flag){
        if(flag){
            mUserinfoAddFriend.setVisibility(View.VISIBLE);
        }else {
            mUserinfoAddFriend.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.userinfo_addFriend, R.id.userinfo_enter_personalPage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userinfo_addFriend:
                showDialog2addFriend();
                break;
            case R.id.userinfo_enter_personalPage:
                break;
        }
    }
}
