package com.wzy.scheduleshare.MainFourPage.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.base.Utils.IMBmobUtils.NewFriendManager;
import com.wzy.scheduleshare.MainFourPage.adapter.FriendListAdapter;
import com.wzy.scheduleshare.MainFourPage.event.RefreshFriendListEvent;
import com.wzy.scheduleshare.MainFourPage.event.RefreshNewFriendEvent;
import com.wzy.scheduleshare.MainFourPage.modle.Friend;
import com.wzy.scheduleshare.MainFourPage.presenter.impl.FriendPagePresenterImpl;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.FriendPagePresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.view.impl.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @ClassName FriendListFragment
 * @Author Wei Zhouye
 * @Date 2020/2/26
 * @Version 1.0
 */
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
        mPresenter = new FriendPagePresenterImpl(this);
        mFriendListAdapter = new FriendListAdapter(getContext(), mList);
        mFriendRecycleview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mFriendRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriendListAdapter.setHasStableIds(true);
        mFriendRecycleview.setAdapter(mFriendListAdapter);
        mPresenter.queryFriends();

        mFriendListAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.UserInfoKey,mList.get(position).getFriendUser());
                intent.putExtra(UserInfoActivity.ShowAddFriendKey,false);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position,View view) {
                showPopMenu(position,view);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_list;
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
        startActivity(new Intent(getActivity(), NewFriendActivity.class));
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
        mList = list;
        mFriendListAdapter.setDataList(mList);
    }

    /*长按展示删除选项*/
    private void showPopMenu(final int position, View view){
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.pop_menu_delete, popupMenu.getMenu());
        popupMenu.setGravity(Gravity.END
        );
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showDeleteDialog(position);
                return true;
            }
        });
    }

    private void showDeleteDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.friendlist_dialog_title);
        builder.setMessage(R.string.friendlist_dialog_msg);
        builder.setPositiveButton(R.string.friendlist_dialog_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name=mList.get(position).getFriendUser().getNickname();
                mList.remove(position);
                refreshFriendList(mList);
                showToast(getString(R.string.friendlist_dialog_delete_success)+name);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.friendlist_dialog_left_btn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
