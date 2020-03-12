package com.wzy.scheduleshare.MainFourPage.presenter.inter;

import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

import java.util.List;

/**
 * @ClassName OtherPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/3/13
 * @Version 1.0
 */
public interface OtherPagePresenter extends IBasePresenter {
    interface View {
        void refreshScheduleList(List<ScheduleDetail> list);  //重新刷新列表

        void updateScheduleList(List<ScheduleDetail> list);  //更新列表

        void finishRefresh(boolean flag); //结束下拉刷新

        void finishLoadMore(boolean flag);  //结束上拉加载

        void initData(User user);
    }

    void refreshList(List<ScheduleDetail> list, User user);  //下拉重新加载

    void loadMore(List<ScheduleDetail> list, User user);  //上拉加载更多
}
