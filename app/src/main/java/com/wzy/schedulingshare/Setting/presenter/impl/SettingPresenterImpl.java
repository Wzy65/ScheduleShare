package com.wzy.schedulingshare.Setting.presenter.impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.wzy.schedulingshare.MainFourPage.event.RefreshUserEvent;
import com.wzy.schedulingshare.base.Utils.TencentCloudUtils.CosServiceFactory;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.Setting.View.SettingActivity;
import com.wzy.schedulingshare.Setting.presenter.inter.SettingPresenter;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;
import com.wzy.schedulingshare.base.view.TakePhotosActivity;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.wzy.schedulingshare.Setting.View.SettingActivity.REQUEST_HEADICON;

/**
 * @ClassName MainFourPagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class SettingPresenterImpl extends BasePresenter<SettingActivity> implements SettingPresenter {
    public SettingPresenterImpl(@NonNull SettingActivity view) {
        super(view);
    }

    private final String bucket2HeadIcon = "schedulingshare-headicon-1301419202";
    private final String accessUrlKey="assessUrlKey";

    private Message msg;
    private  Handler handle=new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what==0){
                final Bundle b=message.getData();
                User user = BmobUser.getCurrentUser(User.class);
                user.setHeadIcon(b.getString(accessUrlKey));
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            mView.updateHeadIcon(b.getString(accessUrlKey));
                            EventBus.getDefault().post(new RefreshUserEvent()); //发送EventBus事件，刷新主页侧拉栏
                        } else {
                            mView.showToast(R.string.setting_update_fail);
                            Logger.i(e.getMessage());
                        }
                    }
                });
            }else {
                mView.showToast(R.string.setting_update_fail+"\n"+(String)msg.obj);
            }
        }
    };


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

    @Override
    public void uploadHeadIcon(final String path) {
        if(path!=null){
            User user = BmobUser.getCurrentUser(User.class);
            upDateHeadIcon(user.getObjectId(),path);

        }

/*        final BmobFile file = new BmobFile(new File(path)); //创建BmobFile对象，转换为Bmob对象
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = BmobUser.getCurrentUser(User.class);
                    user.setHeadIcon(file);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                mView.showToast(R.string.setting_update_success);
                            } else {
                                mView.showToast(R.string.setting_update_fail);
                            }
                        }
                    });
                }else {
                    Logger.i(e.getMessage());
                }
            }

        });*/
    }

    @Override
    public void updateSexy(final String str) {
        User user = BmobUser.getCurrentUser(User.class);
        user.setSexy(str);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mView.showToast(R.string.setting_update_success);
                    mView.updateSexy(str);
                } else {
                    mView.showToast(R.string.setting_update_fail);
                    Log.e(mView.getLocalClassName(), e.getMessage());
                }
            }
        });
    }

    private void upDateHeadIcon(final String cosPath, final String localPath) {
        CosXmlService cosXmlService = CosServiceFactory.getCosXmlServiceWithProperWay(mView, "");

        TransferConfig transferConfig = new TransferConfig.Builder().build();
        /* 初始化 TransferConfig
        * 若有特殊要求，则可以如下进行初始化定制。例如限定当对象 >= 2M 时，启用分块上传，且分块上传的分块大小为1M，当源对象大于5M时启用分块复制，且分块复制的大小为5M。*//*
        TransferConfig transferConfig = new TransferConfig.Builder()
                .setDividsionForCopy(5 * 1024 * 1024) // 是否启用分块复制的最小对象大小
                .setSliceSizeForCopy(5 * 1024 * 1024) // 分块复制时的分块大小
                .setDivisionForUpload(2 * 1024 * 1024) // 是否启用分块上传的最小对象大小
                .setSliceSizeForUpload(1024 * 1024) // 分块上传时的分块大小
                .build();*/
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        //若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket2HeadIcon, cosPath+"_headIcon", localPath, null);


        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                Logger.i("正在上传---------》" + complete / target);
            }
        });
        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                //mView.updateHeadIcon(localPath);
                msg=Message.obtain();
                msg.what=0;
                Bundle b = new Bundle();
                b.putString(accessUrlKey, cOSXMLUploadTaskResult.accessUrl);
                msg.setData(b);
                handle.sendMessage(msg);
                Logger.i("上传成功-----------》"+cOSXMLUploadTaskResult.accessUrl);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                msg=Message.obtain();
                msg.what=-1;
                msg.obj=serviceException.getMessage();
                handle.sendMessage(msg);
                Logger.i("上传失败-----------》"+"\n"+exception.getMessage()+"\n"+serviceException.getMessage());
            }
        });
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                // todo notify transfer state
            }
        });

    }

}
