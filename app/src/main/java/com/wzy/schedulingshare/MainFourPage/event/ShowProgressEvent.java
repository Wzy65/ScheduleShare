package com.wzy.schedulingshare.MainFourPage.event;

/**
 * @ClassName ShowProgressEvent
 * @Author Wei Zhouye
 * @Date 2020/3/8
 * @Version 1.0
 */
public class ShowProgressEvent {
    private boolean flag;

    public ShowProgressEvent(boolean flag) {
        this.flag=flag;
    }

    public boolean getFlag(){
        return flag;
    }
}
