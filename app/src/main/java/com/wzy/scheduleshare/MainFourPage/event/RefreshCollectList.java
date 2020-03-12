package com.wzy.scheduleshare.MainFourPage.event;

import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;

/**
 * @ClassName RefreshCollectList
 * @Author Wei Zhouye
 * @Date 2020/3/13
 * @Version 1.0
 */
public class RefreshCollectList {
    private ScheduleDetail detail;

    public RefreshCollectList(ScheduleDetail detail){
        this.detail=detail;
    }

    public ScheduleDetail getDetail() {
        return detail;
    }

    public void setDetail(ScheduleDetail detail) {
        this.detail = detail;
    }
}
