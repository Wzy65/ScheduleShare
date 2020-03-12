package com.wzy.scheduleshare.MainFourPage.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wzy.scheduleshare.MainFourPage.adapter.ShareDetailListAdapter;
import com.wzy.scheduleshare.MainFourPage.event.RefreshShareScheduleListEvent;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.impl.SharePagePresenterImpl;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.SharePagePresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.view.impl.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

import static com.wzy.scheduleshare.MainFourPage.view.PersonalScheduleDetailActivity.INTENT_TO_PSDA_KEY;
import static com.wzy.scheduleshare.MainFourPage.view.ShareScheduleDetailActivity.INTENT_TO_SSDA_KEY;

/**
 * @ClassName ShareDetailFragment
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public class ShareDetailFragment extends BaseFragment<SharePagePresenter> implements SharePagePresenter.View {

    private List<ScheduleDetail> mList;
    private ShareDetailListAdapter mAdapter;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.share_detail_list)
    RecyclerView mShareDetailList;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share_list;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mPresenter = new SharePagePresenterImpl(this);
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mAdapter = new ShareDetailListAdapter(getContext(), mList);
        mAdapter.setHasStableIds(true);
        mShareDetailList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mShareDetailList.setAdapter(mAdapter);
        mPresenter.refreshList(mList);


        mAdapter.setOnItemClickListener(new ShareDetailListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mList.get(position).getAuth().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                    Intent intent = new Intent(getActivity(), PersonalScheduleDetailActivity.class);
                    intent.putExtra(INTENT_TO_PSDA_KEY, mList.get(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ShareScheduleDetailActivity.class);
                    intent.putExtra(INTENT_TO_SSDA_KEY, mList.get(position));
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(int position, View view) {
            }
        });

        /*设置下拉刷新监听*/
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refreshList(mList);
            }
        });
        /*设置上拉加载更多监听*/
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadMore(mList);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        Logger.i(getClass().getSimpleName() + "------------>onCreateView");
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshShareScheduleListEvent(RefreshShareScheduleListEvent event) {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.finishRefresh(3000);  //延迟3秒后结束刷新
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
}
