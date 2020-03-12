package com.wzy.scheduleshare.MainFourPage.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.adapter.TitleFragmentPagerAdapter;
import com.wzy.scheduleshare.MainFourPage.event.RefreshFriendListEvent;
import com.wzy.scheduleshare.MainFourPage.event.RefreshNewFriendEvent;
import com.wzy.scheduleshare.MainFourPage.event.RefreshShareScheduleListEvent;
import com.wzy.scheduleshare.MainFourPage.event.RefreshUserEvent;
import com.wzy.scheduleshare.MainFourPage.event.ShowProgressEvent;
import com.wzy.scheduleshare.MainFourPage.presenter.impl.MainFourPagePresenterImpl;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.MainFourPagePresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.Setting.View.AboutActivity;
import com.wzy.scheduleshare.Setting.View.AdviceActivity;
import com.wzy.scheduleshare.Setting.View.SettingActivity;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;

/**
 * @ClassName MainActivity
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class MainActivity extends BaseActivity<MainFourPagePresenter> implements MainFourPagePresenter.View {
    @BindView(R.id.nav_logout)
    RelativeLayout mNavLogout;
    @BindView(R.id.nav_retract)
    RelativeLayout mNavRetract;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tablayout)
    TabLayout mTablayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_progress)
    ProgressBar mMainProgress;
    @BindView(R.id.img_logout)
    ImageView mImgLogout;
    @BindView(R.id.img_retract)
    ImageView mImgRetract;
    @BindView(R.id.bottom_layout)
    LinearLayout mBottomLayout;
    @BindView(R.id.nav_head_headIcon)
    ImageView mNavHeadHeadIcon;
    @BindView(R.id.nav_head_nickname)
    TextView mNavHeadNickname;
    @BindView(R.id.nav_head_sexy)
    TextView mNavHeadSexy;
    @BindView(R.id.nav_header)
    LinearLayout mNavHeader;
    @BindView(R.id.nav_head_about)
    TextView mNavHeadAbout;
    @BindView(R.id.nav_head_leaveMsg)
    TextView mNavHeadLeaveMsg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mPresenter = new MainFourPagePresenterImpl(this);
        initTabAndViewPager();
        initNav();
        mFab.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        connectIM();

        EventBus.getDefault().register(this);
    }


    /*
    * 加载tabLayout和Viewpager
    * */
    private void initTabAndViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new FriendListFragment());
        fragments.add(new ShareDetailFragment());
        fragments.add(new PersonalDetailFragment());

        String[] titles = new String[]{getString(R.string.tab_mainPage), getString(R.string.tab_friendPage), getString(R.string.tab_communityPage), getString(R.string.tab_minePage)};

        //设置适配器
        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewpager.setAdapter(adapter);
        //绑定
        mTablayout.setupWithViewPager(mViewpager);
        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 || tab.getPosition() == 3) {
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 || tab.getPosition() == 3) {
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        for (int i = 0; i < mTablayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTablayout.getTabAt(i);
            Drawable d = null;
            switch (i) {
                case 0:
                    d = getResources().getDrawable(R.drawable.home_page);
                    break;
                case 1:
                    d = getResources().getDrawable(R.drawable.friend_page);
                    break;
                case 2:
                    d = getResources().getDrawable(R.drawable.community_page);
                    break;
                case 3:
                    d = getResources().getDrawable(R.drawable.mine_page);
                    break;
            }
            tab.setIcon(d);
        }
    }

    /*
    * 加载侧拉栏
    * */
    private void initNav() {
        refreshNavUser();
    }

    /*
    * 刷新侧拉栏的用户信息，因为有可能经常变，所以单独拿出来，方便在其他地方调用
    * */
    private void refreshNavUser() {
        User user = BmobUser.getCurrentUser(User.class);
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
                into(mNavHeadHeadIcon);
        mNavHeadNickname.setText(String.format(getString(R.string.nav_userinfo_nickname), user.getNickname()));
        mNavHeadSexy.setText(String.format(getString(R.string.nav_userinfo_sexy_txt), user.getSexy()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    /*
    * 动态变换toolbar上的menu控件
    * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        switch (mViewpager.getCurrentItem()) {
            case 1:
                menu.findItem(R.id.action_addFriend).setVisible(true);
                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_addSchedule).setVisible(false);
                EventBus.getDefault().post(new RefreshNewFriendEvent());
                EventBus.getDefault().post(new RefreshFriendListEvent());
                break;
            case 3:
                menu.findItem(R.id.action_addSchedule).setVisible(true);
                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_addFriend).setVisible(false);
                break;
            default:
                menu.findItem(R.id.action_settings).setVisible(true);
                menu.findItem(R.id.action_addSchedule).setVisible(false);
                menu.findItem(R.id.action_addFriend).setVisible(false);
                Logger.i("其他其他");
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.action_addFriend:
                showDialog2addFriend();
                break;
            case R.id.action_addSchedule:
                Intent intent = new Intent(MainActivity.this, PersonalScheduleDetailActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    @OnClick({R.id.nav_logout, R.id.nav_retract, R.id.nav_head_about, R.id.nav_head_leaveMsg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nav_logout:
                finish();
                break;
            case R.id.nav_retract:
                mDrawerLayout.closeDrawers();
                break;
            case R.id.nav_head_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.nav_head_leaveMsg:
                startActivity(new Intent(MainActivity.this, AdviceActivity.class));
                break;
        }
    }


    private void showDialog2addFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setHint(getString(R.string.dialog_add_friend_hint));
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(editText, 20, 10, 20, 10);
        builder.setTitle(getString(R.string.dialog_add_friend_title));
        builder.setPositiveButton(R.string.dialog_add_friend_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPresenter.searchNewFriend(editText.getText().toString())) {
                    dialog.dismiss();
                }
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

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mMainProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectIM();
        //每次进来应用都检查会话和好友请求的情况
        EventBus.getDefault().post(new RefreshNewFriendEvent());
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disConnect();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        EventBus.getDefault().unregister(this);
        mPresenter.clearTCKey();
    }

    /*
    * EvenBus 刷新侧拉栏用户信息
    * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshUserEvent(RefreshUserEvent event) {
        refreshNavUser();
    }

    /*
    * 显示加载
    * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowProgressEvent(ShowProgressEvent event) {
        showProgress(event.getFlag());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /*处理粘性事件，展示服务端返回的内容*/
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receiveSoundRecongnizedmsg(String msg) {
        Logger.i("接收到粘性事件"+msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.push_title));
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.push_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if(stickyEvent!=null){
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

}
