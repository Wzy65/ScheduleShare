package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.SharePagePresenter;
import com.wzy.schedulingshare.MainFourPage.view.ShareDetailFragment;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.Utils.DateUtils;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @ClassName SharePagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public class SharePagePresenterImpl extends BasePresenter<ShareDetailFragment> implements SharePagePresenter {

    private int limit = 10; // 设置查询每页的数据是10条


    public SharePagePresenterImpl(@NonNull ShareDetailFragment view) {
        super(view);
    }


    @Override
    public void refreshList(final List<ScheduleDetail> mList) {
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.order("-updatedAt"); //按照更新时间降序（-表示降序）
        query.setSkip(0);
        query.setLimit(limit);
        query.include("auth");
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    Logger.i("获取新的数据成功"+list.size());
                    if (list.size() > 0) {
                        mList.clear();
                        mList.addAll(list);
                    }
                    mView.finishRefresh(true);
                    mView.refreshScheduleList(mList);
                } else {
                    mView.finishRefresh(false);
                    Logger.i("获取分享行程列表失败：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void loadMore(final List<ScheduleDetail> mList) {
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.order("-updatedAt"); //按照更新时间降序（-表示降序）
        query.setSkip(mList.size());
        query.setLimit(limit);
        query.include("auth");
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    Logger.i("拉取数据成功"+list.size());
                    if (list.size() > 0) {
                        mList.addAll(mList.size(), list);
                    } else {
                        mView.showToast(R.string.share_page_find_no_more);
                    }
                    mView.updateScheduleList(mList);
                    mView.finishLoadMore(true);
                } else {
                    mView.finishLoadMore(false);
                    Logger.i("拉取新的数据失败：" + e.getMessage());
                }
            }
        });
    }
}
