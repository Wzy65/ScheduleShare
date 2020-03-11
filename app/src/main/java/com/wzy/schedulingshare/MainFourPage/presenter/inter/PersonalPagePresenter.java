package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName PersonalPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/3/8
 * @Version 1.0
 */
public interface PersonalPagePresenter extends IBasePresenter {
    interface View {
        void setAllList(List<ScheduleDetail> list);  //设置总的数据

        void refreshScheduleList(List<ScheduleDetail> list);  //刷新列表
    }

    void querySchedule();  //从数据库中读取全部行程

    void deleteDetail(String createTime);  //删除某条记录

    void deleteDetailInBmob(String id,UpdateListener listener);  //删除服务器端的数据

    List<ScheduleDetail> queryScheduleShare(List<ScheduleDetail> details);  //读取已分享的行程

}
