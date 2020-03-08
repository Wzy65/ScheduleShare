package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.sendtion.xrichtext.RichTextEditor;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.PersonalScheduleDetailPresenter;
import com.wzy.schedulingshare.MainFourPage.view.PersonalScheduleDetailActivity;
import com.wzy.schedulingshare.base.Utils.DBUtils;
import com.wzy.schedulingshare.base.Utils.DateUtils;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;
import com.wzy.schedulingshare.base.view.ScheduleTakePhotosActivity;

import java.util.List;

import cn.bmob.v3.BmobUser;

import static com.wzy.schedulingshare.MainFourPage.view.PersonalScheduleDetailActivity.SCHEDULEDETAIL_REQUEST_TAKEPHOTO;
import static com.wzy.schedulingshare.MainFourPage.view.PersonalScheduleDetailActivity.schedule_status;

/**
 * @ClassName PersonalScheduleDetailPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class PersonalScheduleDetailPresenterImpl extends BasePresenter<PersonalScheduleDetailActivity> implements PersonalScheduleDetailPresenter {
    public PersonalScheduleDetailPresenterImpl(@NonNull PersonalScheduleDetailActivity view) {
        super(view);
    }

    private StringBuilder brief;

    @Override
    public void openSysAlbum() {
        Intent intent = new Intent(mView, ScheduleTakePhotosActivity.class);
        intent.putExtra(ScheduleTakePhotosActivity.TAKEPHOTO_TYPE, ScheduleTakePhotosActivity.ON_SELECTPICTURES);
        mView.startActivityForResult(intent, SCHEDULEDETAIL_REQUEST_TAKEPHOTO);
    }

    @Override
    public void openSysCamera() {
        Intent intent = new Intent(mView, ScheduleTakePhotosActivity.class);
        intent.putExtra(ScheduleTakePhotosActivity.TAKEPHOTO_TYPE, ScheduleTakePhotosActivity.ON_TAKEPHOTOS);
        mView.startActivityForResult(intent, SCHEDULEDETAIL_REQUEST_TAKEPHOTO);
    }

    @Override
    public String getEditData(RichTextEditor editor) {
        StringBuilder content = new StringBuilder();
        brief=new StringBuilder();
        try {
            List<RichTextEditor.EditData> editList = editor.buildEditData();
            for (RichTextEditor.EditData itemData : editList) {
                if (itemData.inputStr != null) {
                    content.append(itemData.inputStr);
                    brief.append(itemData.inputStr);
                } else if (itemData.imagePath != null) {
                    content.append("<img src=\"").append(itemData.imagePath).append("\"/>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    @Override
    public void saveTempDetail(String title, String content, String startAt, String endAt) {
        ScheduleDetail detail=new ScheduleDetail();
        detail.setAuth(BmobUser.getCurrentUser(User.class));
        detail.setTitle(title);
        detail.setContent(content);
        detail.setStartAt(startAt);
        detail.setEndAT(endAt);
        detail.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        DBUtils.getINSTANCE(mView).insert2Temp(detail);
        Logger.i("保存临时数据----》"+title);
    }

    @Override
    public void saveDetail(String title, String content, String startAt, String endAt) {
        ScheduleDetail detail=new ScheduleDetail();
        detail.setAuth(BmobUser.getCurrentUser(User.class));
        detail.setTitle(title);
        detail.setContent(content);
        if(brief.toString().length()<=50){
            detail.setBrief(brief.toString());
        }else {
            detail.setBrief(brief.toString().substring(0,51));
        }
        detail.setStartAt(startAt);
        detail.setEndAT(endAt);
        detail.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        detail.setStatus(schedule_status);
        DBUtils.getINSTANCE(mView).insert2Local(detail);
        Logger.i("保存数据----》"+title);
    }

    @Override
    public void checkTemp() {
        ScheduleDetail detail=DBUtils.getINSTANCE(mView.getApplicationContext()).getTempDetail(BmobUser.getCurrentUser(User.class).getObjectId());
        if(detail!=null){
            String start= DateUtils.getDateToString(Long.valueOf(detail.getStartAt()));
            String end= DateUtils.getDateToString(Long.valueOf(detail.getEndAT()));
            mView.showTemp(detail.getTitle(),detail.getContent(),
                    start.substring(0,10),start.substring(11,16),end.substring(0,10),end.substring(11,16));
        }
    }

    @Override
    public void deleteTemp() {
        DBUtils.getINSTANCE(mView).deleteTemp(BmobUser.getCurrentUser(User.class).getObjectId());
        Logger.i("成功保存，删除无用临时信息");
    }
}
