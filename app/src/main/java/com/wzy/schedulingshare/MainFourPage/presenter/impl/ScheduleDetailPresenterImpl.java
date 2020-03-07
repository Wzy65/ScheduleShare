package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.wzy.schedulingshare.MainFourPage.presenter.inter.ScheduleDetailPresenter;
import com.wzy.schedulingshare.MainFourPage.view.ScheduleDetailActivity;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;
import com.wzy.schedulingshare.base.view.TakePhotosActivity;

import static com.wzy.schedulingshare.Setting.View.SettingActivity.REQUEST_HEADICON;

/**
 * @ClassName ScheduleDetailPresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class ScheduleDetailPresenterImpl extends BasePresenter<ScheduleDetailActivity> implements ScheduleDetailPresenter {
    public ScheduleDetailPresenterImpl(@NonNull ScheduleDetailActivity view) {
        super(view);
    }

    @Override
    public void openSysAlbum() {
        Intent intent = new Intent(mView, TakePhotosActivity.class);
        intent.putExtra(TakePhotosActivity.TAKEPHOTO_TYPE, TakePhotosActivity.ON_SELECTPICTURES);
        mView.startActivityForResult(intent, REQUEST_HEADICON);
    }

    @Override
    public void openSysCamera() {
        Intent intent = new Intent(mView, TakePhotosActivity.class);
        intent.putExtra(TakePhotosActivity.TAKEPHOTO_TYPE, TakePhotosActivity.ON_TAKEPHOTOS);
        mView.startActivityForResult(intent, REQUEST_HEADICON);
    }
}
