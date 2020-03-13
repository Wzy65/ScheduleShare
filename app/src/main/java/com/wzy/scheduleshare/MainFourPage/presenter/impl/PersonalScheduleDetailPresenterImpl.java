package com.wzy.scheduleshare.MainFourPage.presenter.impl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.sendtion.xrichtext.RichTextEditor;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.wzy.scheduleshare.MainFourPage.event.RefreshScheduleListEvent;
import com.wzy.scheduleshare.MainFourPage.event.ResetScheduleListEvent;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.PersonalScheduleDetailPresenter;
import com.wzy.scheduleshare.MainFourPage.view.PersonalScheduleDetailActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.Utils.DBUtils;
import com.wzy.scheduleshare.base.Utils.DateUtils;
import com.wzy.scheduleshare.base.Utils.TencentCloudUtils.CosServiceFactory;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;
import com.wzy.scheduleshare.base.view.ScheduleTakePhotosActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.wzy.scheduleshare.MainFourPage.view.PersonalScheduleDetailActivity.SCHEDULEDETAIL_REQUEST_TAKEPHOTO;

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

    private StringBuilder brief = new StringBuilder();

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
        try {
            List<RichTextEditor.EditData> editList = editor.buildEditData();
            for (int i = 0; i < editList.size(); i++) {
                if (!TextUtils.isEmpty(editList.get(i).inputStr)) {
                    content.append(editList.get(i).inputStr);
                    brief.append(editList.get(i).inputStr);
                } else if (editList.get(i).imagePath != null) {
                    content.append("<img src=\"").append(editList.get(i).imagePath).append("\"/>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.i("引用的简略是" + brief.toString());
        return content.toString();
    }

    @Override
    public void saveTempDetail(String title, String content, String startAt, String endAt) {
        ScheduleDetail detail = new ScheduleDetail();
        detail.setAuth(BmobUser.getCurrentUser(User.class));
        detail.setTitle(title);
        detail.setContent(content);
        detail.setStartAt(startAt);
        detail.setEndAT(endAt);
        detail.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        DBUtils.getINSTANCE(mView).insert2Temp(detail);
        Logger.i("保存临时数据----》" + title);
    }

    @Override
    public void saveDetail(String title, String content, String startAt, String endAt, ScheduleDetail d, boolean isUpload, boolean isShare) {
        ScheduleDetail detail;
        if (d != null) {
            detail = d;
        } else {
            detail = new ScheduleDetail();
        }
        detail.setAuth(BmobUser.getCurrentUser(User.class));
        detail.setTitle(title);
        detail.setContent(content);
        String temp = detail.getBrief();
        if (!isShare) {
            temp = brief.toString();
        }
        if (temp.length() <= 60) {
            detail.setBrief(temp);
        } else {
            detail.setBrief(temp.substring(0, 61));
        }
        detail.setStartAt(startAt);
        detail.setEndAT(endAt);
        detail.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        detail.setStatus(mView.getStatus());
        Logger.i("简略是" + detail.getBrief());
        if (d != null) {
            detail.setCreateTime(d.getCreateTime());
            EventBus.getDefault().post(new RefreshScheduleListEvent(detail));
        } else {
            detail.setCreateTime(String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new ResetScheduleListEvent(detail)); //重新设置List并刷新
        }
        if (mView.getStatus().equals("1") && isUpload) {
            detail.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mView.showToast(R.string.schedule_detail_toast_already_share_success);
                    } else {
                        mView.showToast(R.string.schedule_detail_toast_share_update_error);
                        Logger.i("分享更新失败：" + e.getMessage());
                    }
                }
            });
        }
        Logger.i("要插入数据库的id：" + detail.getObjectId());
        DBUtils.getINSTANCE(mView).insert2Local(detail);
        if (mView.getStatus().equals("0")) {
            mView.showToast(R.string.schedule_detail_save_success);
        }
        mView.finishActivity();
        Logger.i("保存数据----》" + title);
    }

    @Override
    public void checkTemp() {
        ScheduleDetail detail = DBUtils.getINSTANCE(mView.getApplicationContext()).getTempDetail(BmobUser.getCurrentUser(User.class).getObjectId());
        if (detail != null) {
            String start = DateUtils.getDateToString(Long.valueOf(detail.getStartAt()));
            String end = DateUtils.getDateToString(Long.valueOf(detail.getEndAT()));
            mView.showTemp(detail.getTitle(), detail.getContent(),
                    start.substring(0, 10), start.substring(11, 16), end.substring(0, 10), end.substring(11, 16));
        }
    }

    @Override
    public void deleteTemp() {
        DBUtils.getINSTANCE(mView).deleteTemp(BmobUser.getCurrentUser(User.class).getObjectId());
        Logger.i("成功保存，删除无用临时信息");
    }

    /*将行程分享出去，上传网络*/
    @Override
    public void shareDetail(RichTextEditor editor, ScheduleDetail detail, String title, String startAt, String endAt) {
        mView.showProgress(true);
        if (detail == null) {
            detail = new ScheduleDetail();
            detail.setCreateTime(String.valueOf(System.currentTimeMillis()));
        }
        detail.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        detail.setAuth(BmobUser.getCurrentUser(User.class));
        detail.setTitle(title);
        detail.setStartAt(startAt);
        detail.setEndAT(endAt);
        detail.setStatus("1");
        new SharUpLoadTask(mView, detail, editor).execute();
    }

    @Override
    public void cancelShare(final ScheduleDetail detail) {
        if (!TextUtils.isEmpty(detail.getObjectId())) {
            mView.showProgress(true);
            ScheduleDetail d = new ScheduleDetail();
            Logger.i("要删除的行程id" + detail.getObjectId());
            Logger.i("上传删除的行程id" + d.getObjectId());
            d.delete(detail.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mView.showToast(R.string.schedule_detail_toast_cancle_share);
                        mView.invalidateOptionsMenu(); //刷新toolbar栏
                        EventBus.getDefault().post(new RefreshScheduleListEvent(detail));
                    } else {
                        mView.showToast(R.string.schedule_detail_toast_cancel_share_error);
                        Logger.i("取消分享失败：" + e.getMessage());
                    }
                    mView.showProgress(false);
                }
            });
        }
    }


    public static class SharUpLoadTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<PersonalScheduleDetailActivity> ref;
        private ScheduleDetail detail;
        private RichTextEditor editor;
        private List<String> photos;
        private BmobException msg;

        public SharUpLoadTask(PersonalScheduleDetailActivity activity, ScheduleDetail d, RichTextEditor ed) {
            if (activity != null) {
                ref = new WeakReference<PersonalScheduleDetailActivity>(activity);
            }
            detail = d;
            editor = ed;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ref == null) {
                return false;
            }
            photos = new ArrayList<>();
            getEditDataPhoto();
            setEditDataPhoto();
            upLoadBmob(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    msg = e;
                    detail.setObjectId(s);
                    ref.get().setDetail(detail);
                    ref.get().saveDetail(detail.getContent(), false, true);
                    Logger.i("行程的id：" + detail.getObjectId());
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (ref.get() == null) {
                return;
            }
            if (!flag) {
                ref.get().showToast(R.string.schedule_detail_toast_share_error + "\n" + msg.getMessage());
            } else {
                ref.get().showToast(R.string.schedule_detail_toast_already_share_success);
            }
            ref.get().showProgress(false);
        }

        private void upLoadBmob(SaveListener<String> listener) {
            detail.save(listener);
        }

        private void setEditDataPhoto() {
            StringBuilder content = new StringBuilder();
            StringBuilder brief = new StringBuilder();
            int index = 0;
            try {
                List<RichTextEditor.EditData> editList = editor.buildEditData();
                for (int i = 0; i < editList.size(); i++) {
                    if (!TextUtils.isEmpty(editList.get(i).inputStr)) {
                        content.append(editList.get(i).inputStr);
                        brief.append(editList.get(i).inputStr);
                    } else if (editList.get(i).imagePath != null) {
                        content.append("<img src=\"").append(photos.get(index++)).append("\"/>");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.i("图片的长度为：" + photos.size());
            Logger.i(content.toString());
            detail.setContent(content.toString());
            if (brief.toString().length() <= 60) {
                detail.setBrief(brief.toString());
            } else {
                detail.setBrief(brief.toString().substring(0, 61));
            }
        }

        private void getEditDataPhoto() {
            List<RichTextEditor.EditData> editList = editor.buildEditData();
            for (int i = 0; i < editList.size(); i++) {
                RichTextEditor.EditData itemData = editList.get(i);
                if (itemData.imagePath != null) {
                    upDatePhoto(BmobUser.getCurrentUser(User.class).getObjectId() + "/" + detail.getCreateTime() + "/" + i, itemData.imagePath);
                }
            }
        }


        private void upDatePhoto(final String cosPath, final String localPath) {
            CosXmlService cosXmlService = CosServiceFactory.getCosXmlServiceWithProperWay(ref.get().getApplicationContext(), "");

            PutObjectRequest putObjectRequest = new PutObjectRequest(CosServiceFactory.bucket2DetailPhoto, cosPath, localPath);

            putObjectRequest.setProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(long progress, long max) {
                }
            });
            // 设置签名校验 Host，默认校验所有 Header
            Set<String> headerKeys = new HashSet<>();
            headerKeys.add("Host");
            putObjectRequest.setSignParamsAndHeaders(null, headerKeys);
            // 使用同步方法上传
            try {
                PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
                photos.add(putObjectResult.accessUrl);
                Logger.i("上传成功，图片的长度为：" + photos.size());
                Logger.i("上传成功-----------》" + putObjectResult.accessUrl);
            } catch (CosXmlClientException e) {
                e.printStackTrace();
                Message msg = Message.obtain();
                mHandler.sendMessage(msg);
                Logger.i("上传失败-----------》" + "\n" + e.getMessage());
            } catch (CosXmlServiceException e) {
                e.printStackTrace();
            }

        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                ref.get().showToast(R.string.upload_photo_fail + "\n" + ref.get().getString(R.string.upload_photo_error));
            }
        };

    }
}
