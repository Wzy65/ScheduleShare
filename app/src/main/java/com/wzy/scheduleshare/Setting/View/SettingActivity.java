package com.wzy.scheduleshare.Setting.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.wzy.scheduleshare.LoginAndRegister.view.LoginActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.Setting.event.LogoutEvent;
import com.wzy.scheduleshare.Setting.presenter.impl.SettingPresenterImpl;
import com.wzy.scheduleshare.Setting.presenter.inter.SettingPresenter;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

import static com.wzy.scheduleshare.LoginAndRegister.view.LoginActivity.NOT_AUTO_LOGIN;
import static com.wzy.scheduleshare.Setting.View.SettingNormalActivity.SETTING_NORMAL_EDIT_ERROR;
import static com.wzy.scheduleshare.Setting.View.SettingNormalActivity.SETTING_NORMAL_EDIT_HINT;
import static com.wzy.scheduleshare.Setting.View.SettingNormalActivity.SETTING_NORMAL_TOOLBAR_TITLE;
import static com.wzy.scheduleshare.Setting.View.SettingNormalActivity.SETTING_NORMAL_TYPE;
import static com.wzy.scheduleshare.base.view.TakePhotosActivity.TakePhotoActivity_Intent_Key;

public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingPresenter.View {

    public static final int REQUEST_HEADICON = 100;
    public static final int REQUEST_NICKNAME = 101;
    public static final int REQUEST_EMAIL = 102;
    public static final int REQUEST_AGE = 103;
    public static final int REQUEST_AREA = 104;
    public static final int REQUEST_PASSWORD = 105;

    private String sexy_choose_result = "男";

    //权限
    public static final int REQUEST_CODE_PERMISSION_CAMERA = 100;
    public static final int REQUEST_CODE_PERMISSION_GALLERY = 101;


    @BindView(R.id.setting_item_1)
    ImageView mSettingItem1;
    @BindView(R.id.setting_item_2)
    ImageView mSettingItem2;
    @BindView(R.id.setting_item_3)
    ImageView mSettingItem3;
    @BindView(R.id.setting_item_4)
    ImageView mSettingItem4;
    @BindView(R.id.setting_item_5)
    ImageView mSettingItem5;
    @BindView(R.id.setting_item_6)
    ImageView mSettingItem6;
    @BindView(R.id.setting_item_7)
    ImageView mSettingItem7;
    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.setting_appbar)
    AppBarLayout mSettingAppbar;
    @BindView(R.id.setting_back_to_login)
    LinearLayout mSettingBackToLogin;
    @BindView(R.id.setting_headIcon_img)
    ImageView mSettingHeadIconImg;
    @BindView(R.id.setting_headIcon)
    LinearLayout mSettingHeadIcon;
    @BindView(R.id.setting_nickname_txt)
    TextView mSettingNicknameTxt;
    @BindView(R.id.setting_nickname)
    LinearLayout mSettingNickname;
    @BindView(R.id.setting_changepw_txt)
    TextView mSettingChangepwTxt;
    @BindView(R.id.setting_changepw)
    LinearLayout mSettingChangepw;
    @BindView(R.id.setting_email_txt)
    TextView mSettingEmailTxt;
    @BindView(R.id.setting_email)
    LinearLayout mSettingEmail;
    @BindView(R.id.setting_sexy_txt)
    TextView mSettingSexyTxt;
    @BindView(R.id.setting_sexy)
    LinearLayout mSettingSexy;
    @BindView(R.id.setting_age_txt)
    TextView mSettingAgeTxt;
    @BindView(R.id.setting_age)
    LinearLayout mSettingAge;
    @BindView(R.id.setting_area_txt)
    TextView mSettingAreaTxt;
    @BindView(R.id.setting_area)
    LinearLayout mSettingArea;
    @BindView(R.id.setting_listview_item)
    LinearLayout mSettingListviewItem;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void initView() {
        mPresenter = new SettingPresenterImpl(this);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标

        if (BmobUser.isLogin()) {
            User user = BmobUser.getCurrentUser(User.class);
            mSettingNicknameTxt.setText(user.getNickname());
            mSettingEmailTxt.setText(user.getEmail());
            mSettingSexyTxt.setText(user.getSexy());
            mSettingAgeTxt.setText(String.valueOf(user.getAge()));
            mSettingAreaTxt.setText(user.getArea());
            String url = mPresenter.getLocalHeadIcon();
            if (TextUtils.isEmpty(url)) {
                url = user.getHeadIcon();
            }
            Glide.with(this).
                    load(url).
                    error(R.drawable.ic_picture_error). //异常时候显示的图片
                    placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                    fallback(R.drawable.login_head).//url为空的时候,显示的图片
                    apply(RequestOptions.bitmapTransform(new CircleCrop())). //显示圆形
                    signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                    into(mSettingHeadIconImg);
        }
        showEditState(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.settings_edit:
                showEditState(true);
                break;

        }
        return true;
    }


    /*设置编辑状态，显示最右边的图标*/
    private void showEditState(boolean flag) {
        if (flag) {
            mSettingItem1.setVisibility(View.VISIBLE);
            mSettingItem2.setVisibility(View.VISIBLE);
            mSettingItem3.setVisibility(View.VISIBLE);
            mSettingItem4.setVisibility(View.VISIBLE);
            mSettingItem5.setVisibility(View.VISIBLE);
            mSettingItem6.setVisibility(View.VISIBLE);
            mSettingItem7.setVisibility(View.VISIBLE);
        } else {
            mSettingItem1.setVisibility(View.GONE);
            mSettingItem2.setVisibility(View.GONE);
            mSettingItem3.setVisibility(View.GONE);
            mSettingItem4.setVisibility(View.GONE);
            mSettingItem5.setVisibility(View.GONE);
            mSettingItem6.setVisibility(View.GONE);
            mSettingItem7.setVisibility(View.GONE);
        }
        mSettingHeadIcon.setEnabled(flag);
        mSettingNickname.setEnabled(flag);
        mSettingChangepw.setEnabled(flag);
        mSettingEmail.setEnabled(flag);
        mSettingSexy.setEnabled(flag);
        mSettingAge.setEnabled(flag);
        mSettingArea.setEnabled(flag);
    }


    @OnClick(R.id.setting_back_to_login)
    public void onViewClicked() {
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        intent.putExtra(NOT_AUTO_LOGIN, NOT_AUTO_LOGIN);
        BmobUser.logOut();
        disConnect();
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LocalHeadIcon",null);
        editor.putString("lastPush",null);
        editor.commit();
        EventBus.getDefault().post(new LogoutEvent());
        startActivity(intent);
        finish();
    }


    @OnClick({R.id.setting_headIcon, R.id.setting_nickname, R.id.setting_changepw, R.id.setting_email, R.id.setting_sexy, R.id.setting_age, R.id.setting_area})
    public void onViewClicked(View view) {
        Intent intent = new Intent(SettingActivity.this, SettingNormalActivity.class);
        switch (view.getId()) {
            case R.id.setting_headIcon:
                showDialog4HeadIcon();
                return;
            case R.id.setting_changepw:
                startActivity(new Intent(SettingActivity.this, SettingPasswordActivity.class));
                return;
            case R.id.setting_sexy:
                showDialog4Sexy();
                return;
            case R.id.setting_nickname:
                intent.putExtra(SETTING_NORMAL_TOOLBAR_TITLE, getString(R.string.setting_nickname));
                intent.putExtra(SETTING_NORMAL_EDIT_HINT, getString(R.string.setting_normal_hint_nickname));
                intent.putExtra(SETTING_NORMAL_EDIT_ERROR, getString(R.string.error_setting_normal_nickname));
                intent.putExtra(SETTING_NORMAL_TYPE, REQUEST_NICKNAME);
                break;
            case R.id.setting_email:
                intent.putExtra(SETTING_NORMAL_TOOLBAR_TITLE, getString(R.string.setting_email));
                intent.putExtra(SETTING_NORMAL_EDIT_HINT, getString(R.string.setting_normal_hint_email));
                intent.putExtra(SETTING_NORMAL_EDIT_ERROR, getString(R.string.error_setting_normal_email));
                intent.putExtra(SETTING_NORMAL_TYPE, REQUEST_EMAIL);
                break;
            case R.id.setting_age:
                intent.putExtra(SETTING_NORMAL_TOOLBAR_TITLE, getString(R.string.setting_age));
                intent.putExtra(SETTING_NORMAL_EDIT_HINT, getString(R.string.setting_normal_hint_age));
                intent.putExtra(SETTING_NORMAL_EDIT_ERROR, getString(R.string.error_setting_normal_age));
                intent.putExtra(SETTING_NORMAL_TYPE, REQUEST_AGE);
                break;
            case R.id.setting_area:
                intent.putExtra(SETTING_NORMAL_TOOLBAR_TITLE, getString(R.string.setting_area));
                intent.putExtra(SETTING_NORMAL_EDIT_HINT, getString(R.string.setting_normal_hint_area));
                intent.putExtra(SETTING_NORMAL_EDIT_ERROR, getString(R.string.error_setting_normal_area));
                intent.putExtra(SETTING_NORMAL_TYPE, REQUEST_AREA);
                break;
        }
        startActivity(intent);
    }

    private void showDialog4HeadIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(getString(R.string.dialog_changeHeadIcon_title));
        String[] choice = {getString(R.string.dialog_changeHeadIcon_camera), getString(R.string.dialog_changeHeadIcon_album)};
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mPresenter.openSysCamera();
                        break;
                    case 1:
                        mPresenter.openSysAlbum();
                }
            }
        });
        builder.create().show();
    }


    private void showDialog4Sexy() {
        final String[] list = {getString(R.string.dialog_sexy_male), getString(R.string.dialog_sexy_female), getString(R.string.dialog_sexy_secret)};
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
        dialog.setTitle(R.string.setting_sexy_dialog_title);
        dialog.setSingleChoiceItems(list, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sexy_choose_result = list[which];
            }
        });
        dialog.setPositiveButton(R.string.setting_sexy_dialog_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mPresenter.updateSexy(sexy_choose_result);
            }
        });
        dialog.setNeutralButton(R.string.setting_sexy_dialog_left_btn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_HEADICON:
                if (resultCode == RESULT_OK) {
                    String imageUri = data.getStringExtra(TakePhotoActivity_Intent_Key);
                    mPresenter.uploadHeadIcon(imageUri);
                    //if(BmobUser.getCurrentUser(User.class).getHeadIcon()!=null) {
                    //   Glide.with(this).load(new File(imageUri)).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(mSettingHeadIconImg);
                    //}
                }
                break;
        }
    }

    @Override
    public void updateSexy(String str) {
        mSettingSexyTxt.setText(str);
        ;
    }

    @Override
    public void updateHeadIcon(String path) {
        showToast(R.string.setting_update_success);
        Glide.with(this).
                load(path).
                error(R.drawable.ic_picture_error). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())). //显示圆形
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(mSettingHeadIconImg);
    }

}
