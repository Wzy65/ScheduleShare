package com.wzy.scheduleshare.MainFourPage.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wzy.scheduleshare.MainFourPage.adapter.ShareDetailListAdapter;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.impl.OtherPagePresenterImpl;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.OtherPagePresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.wzy.scheduleshare.MainFourPage.view.ShareScheduleDetailActivity.INTENT_TO_SSDA_KEY;

/**
 * @ClassName OtherPageActivity
 * @Author Wei Zhouye
 * @Date 2020/3/13
 * @Version 1.0
 */
public class OtherPageActivity extends BaseActivity<OtherPagePresenter> implements OtherPagePresenter.View {

    public static final String INTENT_TO_OTHEPAGE = "INTENT_TO_OTHEPAGE";
    private List<ScheduleDetail> mList;
    private ShareDetailListAdapter mAdapter;
    private User mUser;

    @BindView(R.id.other_page_headIcon)
    ImageView mOtherPageHeadIcon;
    @BindView(R.id.other_page_nickname)
    TextView mOtherPageNickname;
    @BindView(R.id.other_page_sexy)
    TextView mOtherPageSexy;
    @BindView(R.id.other_page_age)
    TextView mOtherPageAge;
    @BindView(R.id.other_page_phonenumber)
    TextView mOtherPagePhonenumber;
    @BindView(R.id.other_page_area)
    TextView mOtherPageArea;
    @BindView(R.id.other_detail_list)
    RecyclerView mOtherDetailList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_other_page;
    }

    @Override
    public void initView() {
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标

        final User user = (User) getIntent().getSerializableExtra(INTENT_TO_OTHEPAGE);
        initData(user);
        mPresenter = new OtherPagePresenterImpl(this);
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mAdapter = new ShareDetailListAdapter(this, mList);
        mAdapter.setHasStableIds(true);
        mOtherDetailList.setLayoutManager(new LinearLayoutManager(this));
        mOtherDetailList.setAdapter(mAdapter);
        mPresenter.refreshList(mList, user);


        mAdapter.setOnItemClickListener(new ShareDetailListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(OtherPageActivity.this, ShareScheduleDetailActivity.class);
                intent.putExtra(INTENT_TO_SSDA_KEY, mList.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View view) {
            }
        });

        /*设置下拉刷新监听*/
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refreshList(mList, user);
            }
        });
        /*设置上拉加载更多监听*/
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadMore(mList, user);
            }
        });
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

    @Override
    public void refreshScheduleList(List<ScheduleDetail> list) {
        mList = list;
        mAdapter.setDataList(list);
    }

    @Override
    public void updateScheduleList(List<ScheduleDetail> list) {
        mList = list;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finishRefresh(boolean flag) {
        if (flag) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void finishLoadMore(boolean flag) {
        if (flag) {
            mRefreshLayout.finishLoadMore();
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }

    @Override
    public void initData(User user) {
        Glide.with(this).
                load(user.getHeadIcon()).
                error(R.drawable.ic_picture_error). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())). //显示圆形
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(mOtherPageHeadIcon);
        mOtherPageNickname.setText(String.format(getString(R.string.other_page_nickname_txt), user.getNickname()));
        mOtherPageAge.setText(String.format(getString(R.string.other_page_age_txt), String.valueOf(user.getAge())));
        mOtherPageArea.setText(String.format(getString(R.string.other_page_area_txt), user.getArea()));
        mOtherPageSexy.setText(String.format(getString(R.string.other_page_sexy_txt), user.getSexy()));
        mOtherPagePhonenumber.setText(String.format(getString(R.string.other_page_phonenum_txt), user.getMobilePhoneNumber()));
    }

}
