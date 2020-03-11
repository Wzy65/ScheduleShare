package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.MainPagePresenter;
import com.wzy.schedulingshare.MainFourPage.view.MainFragment;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.Utils.DBUtils;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * @ClassName MainPagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public class MainPagePresenterImpl extends BasePresenter<MainFragment> implements MainPagePresenter {

    private int limit = 10; // 设置查询每页的数据是10条


    public MainPagePresenterImpl(@NonNull MainFragment view) {
        super(view);
    }


    @Override
    public void queryHappen() {
        mView.refreshHappen(DBUtils.getINSTANCE(mView.getContext()).getHappen(BmobUser.getCurrentUser(User.class).getObjectId()));
    }

    @Override
    public void queryShare(final List<ScheduleDetail> mList) {
        String date = String.valueOf(System.currentTimeMillis());//一天86400秒;
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereGreaterThan("startAt", date);
        query.order("startAt");
        query.setLimit(20);
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        mList.clear();
                        mList.addAll(list);
                        mView.refreshShare(mList);
                    }
                    Logger.i("主页查询动态-最近：" + mList.size());
                } else {
                    Logger.i("主页查询动态-最近失败：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void queryCollect(final List<ScheduleDetail> mList) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect_relation", new BmobPointer(user));
        query.order("updatedAt");
        query.setSkip(0);
        query.setLimit(limit);
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        mList.clear();
                        mList.addAll(list);
                        mView.refreshCollect(mList);
                    }
                } else {
                    Logger.i("主页查询已关注失败：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void loadMoreCollect(final List<ScheduleDetail> mList) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect_relation", new BmobPointer(user));
        query.order("updatedAt");
        query.setSkip(mList.size());
        query.setLimit(limit);
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    Logger.i("主页拉取关注成功" + list.size());
                    if (list.size() > 0) {
                        mList.addAll(mList.size(), list);
                    } else {
                        mView.showToast(R.string.share_page_find_no_more);
                    }
                    mView.updateCollect(mList);
                    mView.finishLoadMore(true);
                } else {
                    mView.finishLoadMore(false);
                    Logger.i("主页拉取关注数据失败：" + e.getMessage());
                }
            }
        });
    }
}
