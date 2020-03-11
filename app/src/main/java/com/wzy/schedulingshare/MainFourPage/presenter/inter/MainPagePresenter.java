package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import java.util.List;

/**
 * @ClassName MainPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public interface MainPagePresenter extends IBasePresenter {
    interface View {
        void refreshCollect(List<ScheduleDetail> list);  //重新刷新列表

        void updateCollect(List<ScheduleDetail> list);  //更新列表

        void finishRefresh(boolean flag); //结束下拉刷新

        void finishLoadMore(boolean flag);  //结束上拉加载

        void refreshHappen(List<ScheduleDetail> list);

        void refreshShare(List<ScheduleDetail> list);

    }

    void queryHappen();  //读取本地即将发生的行程

    void queryShare(List<ScheduleDetail> mList);  //读取动态-更多列表行程

    void queryCollect(List<ScheduleDetail> mList);  //读取关注的行程

    void loadMoreCollect(List<ScheduleDetail> list);  //上拉加载更多


}
