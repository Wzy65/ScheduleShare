package com.wzy.schedulingshare.MainFourPage.view;

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
import com.wzy.schedulingshare.MainFourPage.adapter.MainLHorizontalAdapter;
import com.wzy.schedulingshare.MainFourPage.adapter.ShareDetailListAdapter;
import com.wzy.schedulingshare.MainFourPage.event.RefreshShareScheduleListEvent;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.MainPagePresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.MainPagePresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.view.impl.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

import static com.wzy.schedulingshare.MainFourPage.view.PersonalScheduleDetailActivity.INTENT_TO_PSDA_KEY;
import static com.wzy.schedulingshare.MainFourPage.view.ShareScheduleDetailActivity.INTENT_TO_SSDA_KEY;

/**
 * @ClassName MainFragment
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public class MainFragment extends BaseFragment<MainPagePresenter> implements MainPagePresenter.View {
    private List<ScheduleDetail> happen_list;
    private List<ScheduleDetail> share_list;
    private List<ScheduleDetail> collect_list;
    private MainLHorizontalAdapter happen_adapter;
    private MainLHorizontalAdapter share_adapter;
    private ShareDetailListAdapter collect_adapter;


    @BindView(R.id.main_page_happen_recycleView)
    RecyclerView mMainPageHappenRecycleView;
    @BindView(R.id.main_page_share_recycleView)
    RecyclerView mMainPageShareRecycleView;
    @BindView(R.id.main_page_collect_recycleView)
    RecyclerView mMainPageCollectRecycleView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mPresenter = new MainPagePresenterImpl(this);

        initHappen();
        initShare();
        initCollect();

        /*设置下拉刷新监听*/
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.queryHappen();
                mPresenter.queryShare(share_list);
                mPresenter.queryCollect(collect_list);
                mRefreshLayout.finishRefresh(3000);
            }
        });
        /*设置上拉加载更多监听*/
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadMoreCollect(collect_list);
            }
        });

    }

    /*初始化即将发生列表*/
    private void initHappen() {
        if (happen_list == null) {
            happen_list = new ArrayList<>();
        }
        happen_adapter = new MainLHorizontalAdapter(getContext(), happen_list);
        happen_adapter.setHasStableIds(true);
        mMainPageHappenRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mMainPageHappenRecycleView.setAdapter(happen_adapter);

        mPresenter.queryHappen();

        happen_adapter.setOnItemClickListener(new MainLHorizontalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), PersonalScheduleDetailActivity.class);
                intent.putExtra(INTENT_TO_PSDA_KEY, happen_list.get(position));
                startActivity(intent);
            }
        });
    }

    /*初始化即动态-更多列表*/
    private void initShare() {
        if (share_list == null) {
            share_list = new ArrayList<>();
        }
        share_adapter = new MainLHorizontalAdapter(getContext(), share_list);
        share_adapter.setHasStableIds(true);
        mMainPageShareRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mMainPageShareRecycleView.setAdapter(share_adapter);

        mPresenter.queryShare(share_list);

        share_adapter.setOnItemClickListener(new MainLHorizontalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (share_list.get(position).getAuth().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                    Intent intent = new Intent(getActivity(), PersonalScheduleDetailActivity.class);
                    intent.putExtra(INTENT_TO_PSDA_KEY, share_list.get(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ShareScheduleDetailActivity.class);
                    intent.putExtra(INTENT_TO_SSDA_KEY, share_list.get(position));
                    startActivity(intent);
                }
            }
        });
    }

    /*初始化我的收藏列表*/
    private void initCollect() {
        if (collect_list == null) {
            collect_list = new ArrayList<>();
        }
        collect_adapter = new ShareDetailListAdapter(getContext(), collect_list);
        collect_adapter.setHasStableIds(true);
        mMainPageCollectRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainPageCollectRecycleView.setAdapter(collect_adapter);

        mPresenter.queryCollect(collect_list);

        collect_adapter.setOnItemClickListener(new ShareDetailListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (collect_list.get(position).getAuth().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                    Intent intent = new Intent(getActivity(), PersonalScheduleDetailActivity.class);
                    intent.putExtra(INTENT_TO_PSDA_KEY, collect_list.get(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ShareScheduleDetailActivity.class);
                    intent.putExtra(INTENT_TO_SSDA_KEY, collect_list.get(position));
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(int position, View view) {
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
    }

    @Override
    public void refreshCollect(List<ScheduleDetail> list) {
        collect_list = list;
        collect_adapter.setDataList(collect_list);
    }

    @Override
    public void updateCollect(List<ScheduleDetail> list) {
        collect_list = list;
        collect_adapter.notifyDataSetChanged();
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
    public void refreshHappen(List<ScheduleDetail> list) {
        happen_list = list;
        happen_adapter.setDataList(happen_list);
    }

    @Override
    public void refreshShare(List<ScheduleDetail> list) {
        share_list = list;
        share_adapter.setDataList(share_list);
    }
}
