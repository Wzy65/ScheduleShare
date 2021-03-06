package com.wzy.scheduleshare.MainFourPage.event;

import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;

/**
 * @ClassName RefreshScheduleListEvent
 * @Author Wei Zhouye
 * @Date 2020/3/9
 * @Version 1.0
 */
public class RefreshScheduleListEvent {
    private ScheduleDetail detail;

    public RefreshScheduleListEvent(ScheduleDetail detail) {
        this.detail = detail;
    }

    public ScheduleDetail getDetail() {
        return detail;
    }

}
