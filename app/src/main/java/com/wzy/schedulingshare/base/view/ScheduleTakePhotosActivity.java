package com.wzy.schedulingshare.base.view;

import android.content.Intent;
import android.os.Bundle;

import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.wzy.schedulingshare.base.Utils.TakePhotoHelper;

import java.util.ArrayList;

/**
 * @ClassName TakePhotosActivity
 * @Author Wei Zhouye
 * @Date 2020/2/29
 * @Version 1.0
 */
public class ScheduleTakePhotosActivity extends TakePhotoActivity {
    public static final String TAKEPHOTO_TYPE = "TakePhoto_type";
    public static final String Schedule_TakePhotoActivity_Intent_Compress_Key = "Schedule_TakePhotosActivity_image_compress";
    public static final String Schedule_TakePhotoActivity_Intent_Origin_Key = "Schedule_TakePhotosActivity_image_origin";
    public static final int ON_TAKEPHOTOS = 1;
    public static final int ON_SELECTPICTURES = 2;
    private TakePhotoHelper takePhotoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takePhotoHelper = new TakePhotoHelper(getTakePhoto());
        if (getIntent().getIntExtra(TAKEPHOTO_TYPE, ON_SELECTPICTURES) == ON_SELECTPICTURES) {
            takePhotoHelper.onSelectPictures(2, 1, true, false, 600, 800);
        } else {
            takePhotoHelper.onTakePhotos(true, false, 600, 800);
        }
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        finish();
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showOneImg(result.getImages().get(0));
    }

    /**
     * 只需要一张图片
     */
    private void showOneImg(TImage tImage) {
        // 原图路径：getOriginalPath() 压缩图路径：getCompressPath()
        Intent intent = new Intent();
        intent.putExtra(Schedule_TakePhotoActivity_Intent_Compress_Key, tImage.getCompressPath());
        intent.putExtra(Schedule_TakePhotoActivity_Intent_Origin_Key,tImage.getOriginalPath());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 多张图片
     */
    private void showImg(ArrayList<TImage> images) {
        Intent intent = new Intent();
        intent.putExtra(Schedule_TakePhotoActivity_Intent_Compress_Key, images);
        setResult(RESULT_OK, intent);
        finish();
    }
}
