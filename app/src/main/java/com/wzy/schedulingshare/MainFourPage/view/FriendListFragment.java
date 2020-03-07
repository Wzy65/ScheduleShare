package com.wzy.schedulingshare.MainFourPage.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.base.Utils.IMBmobUtils.NewFriendManager;
import com.wzy.schedulingshare.MainFourPage.adapter.FriendListAdapter;
import com.wzy.schedulingshare.MainFourPage.event.RefreshFriendListEvent;
import com.wzy.schedulingshare.MainFourPage.event.RefreshNewFriendEvent;
import com.wzy.schedulingshare.MainFourPage.modle.Friend;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.FriendPagePresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.FriendPagePresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.view.impl.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FriendListFragment extends BaseFragment<FriendPagePresenter> implements FriendPagePresenter.View {

    private List<Friend> mList;
    private FriendListAdapter mFriendListAdapter;


    @BindView(R.id.friend_listview)
    RecyclerView mFriendRecycleview;
    @BindView(R.id.newFriend_redpoint)
    ImageView mNewFriendRedpoint;
    @BindView(R.id.friend_newFriend)
    LinearLayout mFriendNewFriend;

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mPresenter=new FriendPagePresenterImpl(this);
        mFriendListAdapter = new FriendListAdapter(getContext(),mList);
        mFriendListAdapter.setHasStableIds(true);
        mFriendRecycleview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mFriendRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriendRecycleview.setAdapter(mFriendListAdapter);

        mPresenter.queryFriends();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friendlist;
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


    @OnClick(R.id.friend_newFriend)
    public void onViewClicked() {
        mNewFriendRedpoint.setVisibility(View.GONE);
        startActivity(new Intent(getActivity(),NewFriendActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshNewFriendEvent(RefreshNewFriendEvent event) {
        //好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(getContext()).hasNewFriendInvitation()) {
            mNewFriendRedpoint.setVisibility(View.VISIBLE);
            Logger.i("检查红点，发现新朋友");
        } else {
            mNewFriendRedpoint.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshFriendListEvent(RefreshFriendListEvent event) {
        mPresenter.queryFriends();
    }

    @Override
    public void refreshFriendList(List<Friend> list) {
        mFriendListAdapter.setDataList(list);
    }

}
