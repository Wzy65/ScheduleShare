package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import java.util.List;

/**
 * @ClassName SharePagePresenter
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public interface SharePagePresenter extends IBasePresenter {
    interface View {
        void refreshScheduleList(List<ScheduleDetail> list);  //重新刷新列表

        void updateScheduleList(List<ScheduleDetail> list);  //更新列表

        void finishRefresh(boolean flag); //结束下拉刷新

        void finishLoadMore(boolean flag);  //结束上拉加载
    }

    void refreshList(List<ScheduleDetail> list);  //下拉重新加载

    void loadMore (List<ScheduleDetail> list);  //上拉加载更多
}
