package com.wzy.scheduleshare.MainFourPage.presenter.inter;

import com.sendtion.xrichtext.RichTextEditor;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName PersonalScheduleDetailPresenter
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public interface PersonalScheduleDetailPresenter extends IBasePresenter {
    interface View {
        void showTemp(String title, String content, String startDate, String startTime, String endDate, String endTime);  //恢复上次临时保存的内容

        String getStatus();

        void showProgress(boolean show);

        void setDetail(ScheduleDetail detail);

        void saveDetail(String content, boolean isUpload, boolean isShare);

        void finishActivity();
    }

    void openSysAlbum();

    void openSysCamera();

    String getEditData(RichTextEditor editor);   //获取富文本信息

    void saveTempDetail(String title, String content, String startAt, String endAt); //保存临时信息，避免数据丢失，在onStop调用

    void saveDetail(String title, String content, String startAt, String endAt, ScheduleDetail detail, boolean isUpload, boolean isShare); //保存信息，插入LocalDetailTable表

    void checkTemp();   //检查有无上次保存的临时信息

    void deleteTemp(); //删除临时信息

    void shareDetail(RichTextEditor editor, ScheduleDetail detail, String title, String startAt, String endAt);  //将行程分享出去，上传网络

    void cancelShare(ScheduleDetail detail);
}
