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
public class TakePhotosNotCropActivity extends TakePhotoActivity {
    public static final String TAKEPHOTO_TYPE = "TakePhoto_type";
    public static final String TakePhotoNotCropActivity_Intent_Key="TakePhotosActivity_image";
    public static final int ON_TAKEPHOTOS = 1;
    public static final int ON_SELECTPICTURES = 2;
    private TakePhotoHelper takePhotoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takePhotoHelper = new TakePhotoHelper(getTakePhoto());
        if (getIntent().getIntExtra(TAKEPHOTO_TYPE, ON_SELECTPICTURES) == ON_SELECTPICTURES) {
            takePhotoHelper.onSelectPictures(2, 1, true, 400, 400);
        } else {
            takePhotoHelper.onTakePhotos(true, 400, 400);
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
        intent.putExtra(TakePhotoNotCropActivity_Intent_Key, tImage.getCompressPath());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 多张图片
     */
    private void showImg(ArrayList<TImage> images) {
        Intent intent = new Intent();
        intent.putExtra(TakePhotoNotCropActivity_Intent_Key, images);
        setResult(RESULT_OK, intent);
        finish();
    }
}
