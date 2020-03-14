package com.wzy.scheduleshare.MainFourPage.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.adapter.PersonalDetailListAdapter;
import com.wzy.scheduleshare.MainFourPage.event.RefreshScheduleListEvent;
import com.wzy.scheduleshare.MainFourPage.event.ResetScheduleListEvent;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.impl.PersonalPagePresenterImpl;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.PersonalPagePresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.view.impl.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.wzy.scheduleshare.MainFourPage.view.PersonalScheduleDetailActivity.INTENT_TO_PSDA_KEY;
import static com.wzy.scheduleshare.MainFourPage.view.PersonalScheduleDetailActivity.INTENT_TO_PSDA_Position_KEY;

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
    private int tabSelect = 0;  //记录tablayout选择的位置，方便删除列表某条记录

    @BindView(R.id.personal_page_tablayout)
    TabLayout mPersonalPageTabLayout;
    @BindView(R.id.personal_detail_list)
    RecyclerView mPersonalDetailRecycleView;


    @Override
    public void initView() {
        EventBus.getDefault().register(this);
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
                if (tabSelect == 0) {
                    intent.putExtra(INTENT_TO_PSDA_KEY, mList.get(position));
                } else {
                    intent.putExtra(INTENT_TO_PSDA_KEY, mShareList.get(position));
                }
                intent.putExtra(INTENT_TO_PSDA_Position_KEY, position);
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
                tabSelect = tab.getPosition();
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
        return R.layout.fragment_personal_list;
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


    /*这里返回来的是从列表项中点击进去修改的，说明原本有数据，所以处理不同，多了移除工作*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshScheduleListEvent(RefreshScheduleListEvent event) {
        if(mList==null){
            mList=new ArrayList<>();
        }
        mList.remove(event.getDetail());
        mList.add(0, event.getDetail());  //移到首项
        if (mShareList == null) {
            mShareList = new ArrayList<>();
        }
        mShareList.remove(event.getDetail());
        if (event.getDetail().getStatus().equals("1")) {
            mShareList.add(0, event.getDetail());  //若是状态不变或者有未分享变成分享，则添加到首项
        }
        refreshScheduleList(tabSelect == 0 ? mList : mShareList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetScheduleListEvent(ResetScheduleListEvent event) {
        if (mList != null) {
            mList.add(0, event.getDetail());
        } else {
            mList = new ArrayList<>();
            mList.add(event.getDetail());
        }
        if (event.getDetail().getStatus().equals("1")) {
            if (mShareList != null) {
                mShareList.add(0, event.getDetail());
            } else {
                mShareList = new ArrayList<>();
                mShareList.add(event.getDetail());
            }
        }
        /*这里不用notifydDataChange，是因为存在null的情况，adapter那边没数据刷新不了*/
        refreshScheduleList(tabSelect == 0 ? mList : mShareList);
    }


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
                switch (tabSelect) {
                    case 0:
                        boolean flag = mList.get(position).getStatus().equals("1");
                        if (flag) {
                            mPresenter.deleteDetailInBmob(mList.get(position).getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        mPresenter.deleteDetail(mList.get(position).getCreateTime());
                                        ScheduleDetail d = mList.get(position);
                                        mList.remove(position);
                                        refreshScheduleList(mList);
                                        if (d.getStatus().equals("1")) {
                                            mShareList.remove(d);
                                        }
                                        showToast(getString(R.string.personal_page_delete_success));
                                    } else {
                                        showToast(R.string.personal_page_delete_fail);
                                        Logger.i("删除行程失败："+e.getMessage());
                                    }
                                }
                            });
                        } else {
                            mPresenter.deleteDetail(mList.get(position).getCreateTime());
                            ScheduleDetail d = mList.get(position);
                            mList.remove(position);
                            refreshScheduleList(mList);
                            if (d.getStatus().equals("1")) {
                                mShareList.remove(d);
                            }
                            showToast(getString(R.string.personal_page_delete_success));
                        }
                        break;
                    case 1:
                        mPresenter.deleteDetailInBmob(mShareList.get(position).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    mPresenter.deleteDetail(mShareList.get(position).getCreateTime());
                                    ScheduleDetail detail = mShareList.get(position);
                                    mShareList.remove(position);
                                    mList.remove(detail);
                                    refreshScheduleList(mShareList);
                                    showToast(getString(R.string.personal_page_delete_success));
                                } else {
                                    showToast(R.string.personal_page_delete_fail);
                                    Logger.i("删除行程失败："+e.getMessage());
                                }
                            }
                        });

                        break;
                }
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
    public void setAllList(List<ScheduleDetail> list) {
        mList = list;
    }

    @Override
    public void refreshScheduleList(List<ScheduleDetail> list) {
        mAdapter.setDataList(list);
    }
}
