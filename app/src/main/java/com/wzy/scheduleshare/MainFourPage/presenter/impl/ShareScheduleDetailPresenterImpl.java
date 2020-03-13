package com.wzy.scheduleshare.MainFourPage.presenter.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.event.RefreshCollectList;
import com.wzy.scheduleshare.MainFourPage.modle.Comment;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.ShareScheduleDetailPresenter;
import com.wzy.scheduleshare.MainFourPage.view.ShareScheduleDetailActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName ShareScheduleDetailPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public class ShareScheduleDetailPresenterImpl extends BasePresenter<ShareScheduleDetailActivity> implements ShareScheduleDetailPresenter {
    public ShareScheduleDetailPresenterImpl(@NonNull ShareScheduleDetailActivity view) {
        super(view);
    }

    private int limit = 10; // 设置查询每页的数据是10条


    @Override
    public void refreshDetail(ScheduleDetail detail) {
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", detail.getObjectId());
        query.include("auth");
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.share_schedule_detail_refresh_success);
                    mView.initData(list.get(0));
                    mView.finishRefresh(true);
                } else {
                    mView.showToast(R.string.share_schedule_detail_refresh_fail);
                    Logger.i("分享详情界面刷新失败：" + e.getMessage());
                    mView.finishRefresh(false);
                }
            }
        });
    }

    /*检查用户是否已经收藏该帖子*/
    @Override
    public void checkCollectStatus(final ScheduleDetail detail) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<ScheduleDetail> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect_relation", new BmobPointer(user));
        query.findObjects(new FindListener<ScheduleDetail>() {
            @Override
            public void done(List<ScheduleDetail> list, BmobException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        mView.refreshToolbar(0);
                        Logger.i("查询到收藏失败" + e.getMessage());
                    } else {
                        Logger.i("查询到收藏" + list.size());
                        for (ScheduleDetail scheduleDetail : list) {
                            if (scheduleDetail.getObjectId().equals(detail.getObjectId())) {
                                mView.refreshToolbar(1);
                                Logger.i("查询到收藏相同");
                                return;
                            }
                        }
                    }
                }else {
                    mView.refreshToolbar(0);
                }
            }
        });
    }

    @Override
    public void addCollect(ScheduleDetail detail) {
        if (detail == null) {
            mView.showToast(R.string.share_schedule_detail_collect_fail);
            return;
        }
        User user = BmobUser.getCurrentUser(User.class);
        if (detail.getAuth() != null && !TextUtils.isEmpty(detail.getObjectId()) && detail.getAuth().getObjectId().equals(user.getObjectId())) {
            mView.showToast(R.string.share_schedule_detail_collect_error);
            return;
        }
        BmobRelation relation = new BmobRelation();
        relation.add(detail);
        user.setCollect_relation(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.share_schedule_detail_collect_success);
                    mView.refreshToolbar(1);
                } else {
                    mView.showToast(R.string.share_schedule_detail_collect_fail);
                    mView.refreshToolbar(0);
                }
            }
        });
    }

    @Override
    public void cancelCollect(final ScheduleDetail detail) {
        if (detail == null) {
            mView.showToast(R.string.share_schedule_detail_collect_cancel_fail);
            return;
        }
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.remove(detail);
        user.setCollect_relation(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.share_schedule_detail_collect_cancel_success);
                    mView.refreshToolbar(0);
                    EventBus.getDefault().post(new RefreshCollectList(detail));
                } else {
                    mView.showToast(R.string.share_schedule_detail_collect_cancel_fail);
                    mView.refreshToolbar(1);
                }
            }
        });
    }

    @Override
    public void addComment(String content, final ScheduleDetail detail) {
        if (TextUtils.isEmpty(content)) {
            mView.showToast(R.string.share_schedule_detail_comment_add_error);
            return;
        }
        final Comment comment = new Comment();
        comment.setAuth(BmobUser.getCurrentUser(User.class));
        comment.setContent(content);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    comment.setObjectId(s);
                    BmobRelation relation = new BmobRelation();
                    relation.add(comment);
                    detail.setComment_relation(relation);
                    detail.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                mView.refresh();
                                mView.showToast(R.string.share_schedule_detail_comment_add_success);
                            } else {
                                mView.showToast(mView.getString(R.string.share_schedule_detail_comment_add_fail));
                            }
                        }
                    });
                } else {
                    mView.showToast(mView.getString(R.string.share_schedule_detail_comment_add_fail));
                }
            }
        });
        //comment.setDetail(detail);

    }


    @Override
    public void refreshList(ScheduleDetail detail, final List<Comment> mList) {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereRelatedTo("comment_relation", new BmobPointer(detail));
        query.order("-updatedAt");
        query.setLimit(limit);
        query.include("auth");
        query.setSkip(0);
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        mList.clear();
                        mList.addAll(list);
                        mView.refreshCommentList(mList);
                    }
                    mView.finishLoadMore(true);
                } else {
                    mView.finishRefresh(false);
                    Logger.i("评论列表下拉更新失败：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void loadMore(ScheduleDetail detail, final List<Comment> mList) {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereRelatedTo("comment_relation", new BmobPointer(detail));
        query.order("-updatedAt");
        query.setLimit(limit);
        query.setSkip(mList.size());
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        mList.addAll(mList.size(), list);
                        mView.updateCommentList(mList);
                    } else {
                        mView.showToast(R.string.share_page_find_no_more);
                    }
                    mView.finishLoadMore(true);
                } else {
                    mView.finishLoadMore(false);
                    Logger.i("评论列表界面上拉刷新失败：" + e.getMessage());
                }
            }
        });
    }
}
