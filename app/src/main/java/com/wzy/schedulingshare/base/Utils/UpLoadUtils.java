package com.wzy.schedulingshare.base.Utils;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
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
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.wzy.schedulingshare.base.Utils.TencentCloudUtils.COSConfigManager;
import com.wzy.schedulingshare.base.Utils.TencentCloudUtils.CosServiceFactory;

/**
 * @ClassName UpLoadUtils
 * @Author Wei Zhouye
 * @Date 2020/3/2
 * @Version 1.0
 */
public class UpLoadUtils {
    private UpLoadUtils() {
    }

    private static final String bucket2HeadIcon = "schedulingshare-headicon-1301419202";


    public static void upDateHeadIcon(Context context, String cosPath, String localPath) {
        CosXmlService cosXmlService = CosServiceFactory.getCosXmlServiceWithProperWay(context, "");

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
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket2HeadIcon, cosPath, localPath, null);


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
                Logger.i("上传成功-----------》"+cOSXMLUploadTaskResult.accessUrl);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                //listenser.onFail(serviceException.getMessage());
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
