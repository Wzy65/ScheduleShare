package com.wzy.schedulingshare.MainFourPage.event;

import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;

/**
 * @ClassName ResetScheduleListEvent
 * @Author Wei Zhouye
 * @Date 2020/3/9
 * @Version 1.0
 */
public class ResetScheduleListEvent {

    ScheduleDetail detail;

    public ResetScheduleListEvent(ScheduleDetail detail){
        this.detail=detail;
    }


    public ScheduleDetail getDetail() {
        return detail;
    }
}
