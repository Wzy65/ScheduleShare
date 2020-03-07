package com.wzy.schedulingshare.MainFourPage.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.base.Utils.IMBmobUtils.NewFriendManager;
import com.wzy.schedulingshare.MainFourPage.adapter.NewFriendListAdapter;
import com.wzy.schedulingshare.MainFourPage.event.RefreshFriendListEvent;
import com.wzy.schedulingshare.MainFourPage.modle.NewFriend;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.NewFriendPresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.NewFriendPresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * @ClassName NewFriendActivity
 * @Author Wei Zhouye
 * @Date 2020/3/5
 * @Version 1.0
 */
public class NewFriendActivity extends BaseActivity<NewFriendPresenter> implements NewFriendPresenter.View {

    private NewFriendListAdapter mAdapter;
    private boolean refresh_flag=true;
    private List<NewFriend> mList;

    @BindView(R.id.newFriend_progress)
    ProgressBar mNewFriendProgress;
    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.newFriend_recycleView)
    RecyclerView mNewFriendRecycleView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_newfriend;
    }

    @Override
    public void initView() {
        mPresenter = new NewFriendPresenterImpl(this);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标

        mAdapter = new NewFriendListAdapter(this, mList, mPresenter);
        mAdapter.setHasStableIds(true);
        mNewFriendRecycleView.setAdapter(mAdapter);
        mNewFriendRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mNewFriendRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        NewFriendManager.getInstance(this).updateBatchStatus();

        mAdapter.setOnItemClickListener(new NewFriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("NewFirendActivity", "----------->点击了第" + position + "个item");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        query();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newfriend_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.newfriend_menu_refresh:
                if (refresh_flag) {
                    Logger.i("刷新好友列表");
                    refresh_flag = false;
                    query();
                }
                break;
        }
        return true;
    }


    /**
     * 查询本地会话
     */
    public void query() {
        showProgress(true);
        mAdapter.setDataList(NewFriendManager.getInstance(this).getAllNewFriend());
        //Logger.i("共有"+mList.size()+"条新的好友请求");
        refresh_flag = true;
        showProgress(false);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mNewFriendProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mNewFriendProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNewFriendProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mNewFriendProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().post(new RefreshFriendListEvent());
    }

}
