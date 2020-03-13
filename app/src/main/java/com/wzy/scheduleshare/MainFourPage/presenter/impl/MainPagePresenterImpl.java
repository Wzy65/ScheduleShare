package com.wzy.scheduleshare.MainFourPage.presenter.impl;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.MainPagePresenter;
import com.wzy.scheduleshare.MainFourPage.view.MainFragment;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.Utils.DBUtils;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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
        String date = String.valueOf(System.currentTimeMillis());
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereGreaterThan("startAt", date);
        query.order("startAt");
        query.include("auth");
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
        final BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect_relation", new BmobPointer(user));
        query.order("updatedAt");
        //query.include("auth"); 这里很奇怪，返回来的数据没有用户信息，评论区的却可以，很神奇
        query.setSkip(0);
        query.setLimit(limit);
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(final List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        new Runnable() {
                            @Override
                            public void run() {
                                for (final ScheduleDetail scheduleDetail : list) {
                                    BmobQuery<User> query1 = new BmobQuery<User>();
                                    query1.addWhereEqualTo("objectId", scheduleDetail.getAuth().getObjectId());
                                    query1.findObjects(new FindListener<User>() {
                                        @Override
                                        public void done(List<User> list, BmobException e) {
                                            if (e == null && list.size() > 0) {
                                                scheduleDetail.setAuth(list.get(0));
                                            }
                                        }
                                    });
                                }
                                mList.clear();
                                mList.addAll(list);
                                mView.refreshCollect(mList);
                            }
                        }.run();
                        mView.updateCollect(mList);
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
            public void done(final List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    Logger.i("主页拉取关注成功" + list.size());
                    if (list.size() > 0) {
                        new Runnable() {
                            @Override
                            public void run() {
                                for (final ScheduleDetail scheduleDetail : list) {
                                    BmobQuery<User> query1 = new BmobQuery<User>();
                                    query1.addWhereEqualTo("objectId", scheduleDetail.getAuth().getObjectId());
                                    query1.findObjects(new FindListener<User>() {
                                        @Override
                                        public void done(List<User> list, BmobException e) {
                                            if (e == null && list.size() > 0) {
                                                scheduleDetail.setAuth(list.get(0));
                                            }
                                        }
                                    });
                                }
                                mList.addAll(mList.size(), list);
                            }
                        }.run();
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

    private Handler handle=new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0:
                    break;
                case 1:
                    break;
            }
        }
    };

}
