package com.wzy.schedulingshare.MainFourPage.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.adapter.PersonalDetailListAdapter;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.PersonalPagePresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.PersonalPagePresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.view.impl.BaseFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wzy.schedulingshare.MainFourPage.view.PersonalScheduleDetailActivity.INTENT_TO_PSDA_KEY;

/**
 * @ClassName PersonalDetailFragment
 * @Author Wei Zhouye
 * @Date 2020/3/8
 * @Version 1.0
 */
public class PersonalDetailFragment extends BaseFragment<PersonalPagePresenter> implements PersonalPagePresenter.View {


    private List<ScheduleDetail> mList;
    private List<ScheduleDetail> mShareList;
    private PersonalDetailListAdapter mAdapter;

    @BindView(R.id.personal_page_tablayout)
    TabLayout mPersonalPageTabLayout;
    @BindView(R.id.personal_detail_list)
    RecyclerView mPersonalDetailRecycleView;


    @Override
    public void initView() {
        //EventBus.getDefault().register(this);
        mPresenter = new PersonalPagePresenterImpl(this);
        mAdapter = new PersonalDetailListAdapter(getContext(), mList);
        mAdapter.setHasStableIds(true);
        mPersonalDetailRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPersonalDetailRecycleView.setAdapter(mAdapter);

        mPresenter.querySchedule();

        mAdapter.setOnItemClickListener(new PersonalDetailListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), PersonalScheduleDetailActivity.class);
                intent.putExtra(INTENT_TO_PSDA_KEY, mList.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position, View view) {
                showPopMenu(position, view);
            }
        });

        mPersonalPageTabLayout.addTab(mPersonalPageTabLayout.newTab().setText(getString(R.string.personal_page_all)));
        mPersonalPageTabLayout.addTab(mPersonalPageTabLayout.newTab().setText(getString(R.string.personal_page_share)));
        mPersonalPageTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    if (mList != null) {
                        refreshScheduleList(mList);
                    } else {
                        mPresenter.querySchedule();
                    }
                } else {
                    if (mShareList == null) {
                        mShareList = mPresenter.queryScheduleShare(mList);
                    }
                    refreshScheduleList(mShareList);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_detail;
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
        //EventBus.getDefault().unregister(this);
    }




/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshFriendListEvent(RefreshFriendListEvent event) {
        mPresenter.queryFriends();
    }

    @Override
    public void refreshFriendList(List<Friend> list) {
        mList = list;
        mFriendListAdapter.setDataList(mList);
    }*/

    /*长按展示删除选项*/
    private void showPopMenu(final int position, View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.pop_menu_delete, popupMenu.getMenu());
        popupMenu.setGravity(Gravity.END);
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

    private void showDeleteDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.personal_page_dialog_title);
        builder.setMessage(R.string.personal_page_dialog_msg);
        builder.setPositiveButton(R.string.personal_page_dialog_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mList.remove(position);
                //refreshFriendList(mList);
                showToast(getString(R.string.personal_page_delete_success));
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.personal_page_dialog_left_btn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void refreshScheduleList(List<ScheduleDetail> list) {
        mList = list;
        mAdapter.setDataList(mList);
    }
}
