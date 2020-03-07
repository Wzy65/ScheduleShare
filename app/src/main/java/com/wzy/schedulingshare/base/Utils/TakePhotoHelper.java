package com.wzy.schedulingshare.base.Utils;

import android.net.Uri;
import android.os.Environment;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * @ClassName TakePhotoHelper
 * @Author Wei Zhouye
 * @Date 2020/2/29
 * @Version 1.0
 */
public class TakePhotoHelper {

    private TakePhoto takePhoto;
    private Uri imageUri;

    public TakePhotoHelper(TakePhoto takePhoto) {
        this.takePhoto = takePhoto;
        initImageUri();
        // 默认配置，需要特定设置可以在新建 TakePhotoHelper 后调用
        // sumsung手机存在图片旋转角度问题，暂时没有解决
        configCompress(true, 102400, 800, 800, false, false, false);
        configTakePhotoOption(false, true);
    }

    private void initImageUri() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "/takephototemp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        imageUri = Uri.fromFile(file);
    }

    /**
     * 拍照
     *
     * @param crop   是否裁切
     * @param width  宽
     * @param height 高
     */
    public void onTakePhotos(boolean crop, int width, int height) {
        if (crop) {
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions(true, true, false, width, height));
        } else {
            takePhoto.onPickFromCapture(imageUri);
        }
    }

    /**
     * 选择图片
     *
     * @param imageType 图片选择途径（1：文件 2：相册）
     * @param etLimit   最多选择图片数量
     * @param crop      是否裁切
     * @param width     宽
     * @param height    高
     */
    public void onSelectPictures(int imageType, int etLimit, boolean crop, int width, int height) {
        if (etLimit > 1) {
            if (crop) {
                takePhoto.onPickMultipleWithCrop(etLimit, getCropOptions(true, true, false, width, height));
            } else {
                takePhoto.onPickMultiple(etLimit);
            }
            return;
        }
        if (imageType == 1) {
            if (crop) {
                takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions(true, true, false, width, height));
            } else {
                takePhoto.onPickFromDocuments();
            }
        } else {
            if (crop) {
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions(true, true, false, width, height));
            } else {
                takePhoto.onPickFromGallery();
            }
        }
    }

    /**
     * 压缩配置
     *
     * @param compress        是否压缩
     * @param maxSize         大小不超过（B）（1M=1024KB 1Kb=1024B）
     * @param width           宽
     * @param height          高
     * @param showProgressBar 是否显示压缩进度条
     * @param saveRawFile     拍照压缩后是否保存原图
     * @param useLuban        选择压缩工具（自带或者Luban）
     */
    public void configCompress(boolean compress, int maxSize, int width, int height,
                               boolean showProgressBar, boolean saveRawFile, boolean useLuban) {
        if (!compress) {
            takePhoto.onEnableCompress(null, false);
            return;
        }

        CompressConfig config;
        if (!useLuban) {
            config = new CompressConfig
                    .Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(saveRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions
                    .Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(saveRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    /**
     * 相册配置
     *
     * @param useOwnGallery 是否使用TakePhoto自带相册
     * @param correctImage  是否纠正拍照的照片旋转角度
     */
    public void configTakePhotoOption(boolean useOwnGallery, boolean correctImage) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(useOwnGallery);
        builder.setCorrectImage(correctImage);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    /**
     * 裁切配置
     *
     * @param crop       是否裁切
     * @param useOwnCrop 选择裁切工具（第三方或者TakePhoto自带）
     * @param aspect     设置尺寸/比例（宽x高或者宽/高）
     * @param width      宽
     * @param height     高
     */
    private CropOptions getCropOptions(boolean crop, boolean useOwnCrop, boolean aspect, int width, int height) {
        if (!crop) {
            return null;
        }

        CropOptions.Builder builder = new CropOptions.Builder();
        if (aspect) {
            builder.setAspectX(width).setAspectY(height);
        } else {
            builder.setOutputX(width).setOutputY(height);
        }
        builder.setWithOwnCrop(useOwnCrop);
        return builder.create();
    }

}
