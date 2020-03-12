package com.wzy.scheduleshare.MainFourPage.presenter.inter;

import com.wzy.scheduleshare.MainFourPage.modle.Comment;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

import java.util.List;

/**
 * @ClassName ShareScheduleDetailPresenter
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public interface ShareScheduleDetailPresenter extends IBasePresenter {
    interface View {
        void finishRefresh(boolean flag); //结束下拉刷新

        void finishLoadMore(boolean flag);  //结束上拉加载

        void initData(final ScheduleDetail detail);

        void refresh();  //刷新界面

        void refreshCommentList(List<Comment> list);  //重新刷新列表

        void updateCommentList(List<Comment> list);

        void refreshToolbar(int falg);  //刷新菜单栏
    }

    void refreshDetail(final ScheduleDetail detail);

    void addComment(String content, ScheduleDetail detail);

    void refreshList(ScheduleDetail detail, List<Comment> list);  //下拉重新加载

    void loadMore(ScheduleDetail detail, List<Comment> list);  //上拉加载更多

    void checkCollectStatus(ScheduleDetail detail);  //检查是否已经收藏过行程

    void addCollect(ScheduleDetail detail);  //收藏/关注

    void cancelCollect(ScheduleDetail detail); //取消收藏/关注
}
