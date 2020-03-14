package com.wzy.scheduleshare.MainFourPage.presenter.impl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
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
import com.tencent.cos.xml.model.tag.eventstreaming.Progress;
import com.tencent.cos.xml.utils.StringUtils;
import com.wzy.scheduleshare.MainFourPage.modle.Backup;
import com.wzy.scheduleshare.MainFourPage.modle.Friend;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.MainFourPagePresenter;
import com.wzy.scheduleshare.MainFourPage.view.MainActivity;
import com.wzy.scheduleshare.MainFourPage.view.UserInfoActivity;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.Utils.DBUtils;
import com.wzy.scheduleshare.base.Utils.TencentCloudUtils.CosServiceFactory;
import com.wzy.scheduleshare.base.Utils.XRichTextStringUtils;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.presenter.impl.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName MainFourPagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class MainFourPagePresenterImpl extends BasePresenter<MainActivity> implements MainFourPagePresenter {
    public MainFourPagePresenterImpl(@NonNull MainActivity view) {
        super(view);
    }

    @Override
    public boolean searchNewFriend(String phoneNumber) {
        if (!isPhoneNumberValid(phoneNumber)) {
            mView.showToast(R.string.error_mistake_phoneNumber);
            return false;
        }
        if (phoneNumber.equals(BmobUser.getCurrentUser(User.class).getMobilePhoneNumber())) {
            mView.showToast(R.string.error_mistake_isUser);
            return false;
        }
        mView.showProgress(true);
        queryUser(phoneNumber);
        return true;
    }

    @Override
    public String getLocalHeadIcon() {
        SharedPreferences settings = mView.getSharedPreferences("UserInfo", 0);
        return settings.getString("LocalHeadIcon", null);
    }

    @Override
    public void clearTCKey() {
        SharedPreferences settings = mView.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user", "");
        editor.putString("appid", "");
        editor.putString("secretId", "");
        editor.putString("secretKey", "");
        editor.commit();
    }

    @Override
    public void backUp() {
        new BackupTask(mView).execute();
    }

    @Override
    public void recovery() {
        new RecoveryTask(mView).execute();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 查询用户表
     */
    private void queryUser(final String phoneNumber) {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("mobilePhoneNumber", phoneNumber);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (object == null || object.size() == 0) {
                    mView.showToast(R.string.error_non_exist_user);
                } else {
                    if (e == null) {
                        checkAddAlready(object.get(0));//mobilePhoneNumber为唯一键，所以只需要获取第一个
                    } else {
                        mView.showToast("查询失败" + e.getMessage());
                        Logger.i("查询失败" + e.getMessage());
                    }
                }
                mView.showProgress(false);
            }
        });
    }

    private void checkAddAlready(final User user) {
        BmobQuery<Friend> bmobQuery = new BmobQuery<>();
        BmobQuery<Friend> bq1 = new BmobQuery<>();
        bq1.addWhereEqualTo("user", user);
        BmobQuery<Friend> bq2 = new BmobQuery<>();
        bq2.addWhereEqualTo("friendUser", BmobUser.getCurrentUser(User.class));
        List<BmobQuery<Friend>> list = new ArrayList<>();
        list.add(bq1);
        list.add(bq2);
        bmobQuery.and(list);  //复合查询
        bmobQuery.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> object, BmobException e) {
                boolean flag = false;
                if (object == null || object.size() == 0) {
                    flag = e == null;   //查询成功后，从Friend表找不到关联，说明该用户不是本地用户的好友，新页面展示添加按钮。
                }
                Logger.i("查找到" + object.size() + "个好友" + flag);
                Intent intent = new Intent(mView, UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.UserInfoKey, user);
                intent.putExtra(UserInfoActivity.ShowAddFriendKey, flag);
                mView.startActivity(intent);
                if (e != null) {
                    Logger.i(e.getMessage());
                }
            }
        });
    }


    /*恢复任务*/

    private class RecoveryTask extends AsyncTask<Void, Integer, Boolean> {
        private WeakReference<MainActivity> ref;
        private User user;
        private boolean isSuccess;

        public RecoveryTask(MainActivity activity) {
            ref = new WeakReference<MainActivity>(activity);
            user = BmobUser.getCurrentUser(User.class);
            isSuccess = true;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ref.get() == null) {
                publishProgress(100);
                return false;
            }
            BmobQuery<Backup> query = new BmobQuery<>();
            query.addWhereEqualTo("auth", user);
            query.findObjects(new FindListener<Backup>() {
                @Override
                public void done(List<Backup> list, BmobException e) {
                    if (e == null) {
                        if (list.size() == 0) {
                            Message msg = Message.obtain();
                            msg.obj = ref.get().getString(R.string.recovery_get_zero);
                            mHandler.sendMessage(msg);
                            publishProgress(100);
                        } else {
                            insert2Local(list);
                            publishProgress(60);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.obj = ref.get().getString(R.string.recovery_get_error);
                        publishProgress(100);
                        isSuccess = false;
                    }
                }
            });

            publishProgress(100);
            return isSuccess;

        }

        @MainThread
        protected void onProgressUpdate(Integer... progresses) {
            ref.get().setProgressRate(progresses[0]);
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (ref.get() == null) {
                return;
            }
            if (isSuccess) {
                ref.get().showToast(R.string.recovery_success);
            } else {
                ref.get().showToast(R.string.recovery_fail);
            }
        }

        private void insert2Local(List<Backup> list) {
            int rate = 60;
            int addRate = 40 / list.size();
            for (Backup backup : list) {
                ScheduleDetail detail = new ScheduleDetail(backup);
                detail.setAuth(user);
                DBUtils.getINSTANCE(ref.get()).insert2Local(detail);
                rate += addRate;
                publishProgress(rate);
            }
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                ref.get().showToast((String) message.obj);
            }
        };

    }


    /*备份任务*/
    private class BackupTask extends AsyncTask<Void, Integer, Boolean> {

        private List<String> photos;
        private WeakReference<MainActivity> ref;
        private int rate = 0;
        private User user;
        private boolean isSuccess;
        private CountDownLatch countDownLatch;
        private boolean isEmptyDetail=false;

        public BackupTask(MainActivity activity) {
            ref = new WeakReference<MainActivity>(activity);
            user = BmobUser.getCurrentUser(User.class);
            isSuccess = true;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ref.get() == null) {
                publishProgress(100);
                return false;
            }
            List<ScheduleDetail> list = DBUtils.getINSTANCE(ref.get()).getAllDetail(user.getObjectId());
            if(list==null || list.size()==0){
                publishProgress(100);
                Message msg=Message.obtain();
                msg.what=1;
                msg.obj=ref.get().getString(R.string.backup_detail_error);
                mHandler.sendMessage(msg);
                isEmptyDetail=true;
                return false;
            }
            countDownLatch = new CountDownLatch(list.size());
            if (list == null) {
                publishProgress(100);
                return false;
            }
            rate += 10;
            publishProgress(rate);
            int addRate1 = 50 / list.size();
            int addRate2 = 40 / list.size();
            for (ScheduleDetail d : list) {
                photos = getEditDataPhoto(d, addRate1); //将本地图片上传网络并得到url
                setEditDataPhoto(d, photos);  //将url替换本地图片的地址
                upLoadBmob(d, addRate2);
            }

                        /*阻塞，所有的上传成功后再执行下面的过程*/
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            publishProgress(100);
            return isSuccess;
        }

        @MainThread
        protected void onProgressUpdate(Integer... progresses) {
            ref.get().setProgressRate(progresses[0]);
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (ref.get() == null) {
                return;
            }
            if (flag) {
                ref.get().showToast(R.string.backup_success);
            } else if(!flag && !isEmptyDetail){
                ref.get().showToast(R.string.backup_fail);
            }
        }

        private List<String> getEditDataPhoto(ScheduleDetail d, int addRate) {
            List<String> imagList=new ArrayList<>();
            List<String> photos = XRichTextStringUtils.getTextFromHtml(d.getContent(), true);
            if (photos.size() == 0) {
                rate += addRate;
                publishProgress(addRate);
                return imagList;
            }
            addRate = addRate / photos.size();
            int i = 0;
            for (String path : photos) {
                if(path.startsWith("http")){
                    imagList.add(path);
                    continue;
                }
                upDatePhoto(BmobUser.getCurrentUser(User.class).getObjectId() + "/" + d.getCreateTime() + "/" + i, path, imagList);
                rate += addRate;
                publishProgress(rate);
                i++;
            }
            return imagList;
        }


        private void upLoadBmob(final ScheduleDetail d, final int addRate) {
            /*先检查是否以已存在，避免重复数据*/
            BmobQuery<Backup> bq1 = new BmobQuery<>();
            bq1.addWhereEqualTo("auth", user);
            BmobQuery<Backup> bq2 = new BmobQuery<>();
            bq2.addWhereEqualTo("createTime", d.getCreateTime());
            List<BmobQuery<Backup>> list = new ArrayList<>();
            list.add(bq1);
            list.add(bq2);
            BmobQuery<Backup> query = new BmobQuery<>();
            query.and(list);
            query.findObjects(new FindListener<Backup>() {
                @Override
                public void done(List<Backup> list, BmobException e) {
                    if (e == null) {
                        final Backup backup = new Backup(d);
                        BmobACL acl = new BmobACL();  //创建ACL对象
                        acl.setReadAccess(user, true); // 设置当前用户可写的权限
                        acl.setWriteAccess(user, true); // 设置当前用户可写的权限
                        backup.setACL(acl);

                        if (list.size() == 0) {
                            backup.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e != null) {
                                        isSuccess = false;
                                        Message msg = Message.obtain();
                                        msg.obj = backup.getTitle();
                                        msg.what=0;
                                        mHandler.sendMessage(msg);
                                        Logger.i("备份创建失败："+e.getMessage());
                                    } else {
                                        backup.setObjectId(s);
                                    }
                                }
                            });
                        } else {
                            backup.setObjectId(list.get(0).getObjectId());
                            backup.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e != null) {
                                        isSuccess = false;
                                        Message msg = Message.obtain();
                                        msg.obj = backup.getTitle();
                                        msg.what=0;
                                        mHandler.sendMessage(msg);
                                        Logger.i("备份更新失败："+e.getMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        isSuccess = false;
                        Message msg = Message.obtain();
                        msg.obj = d.getTitle();
                        msg.what=0;
                        mHandler.sendMessage(msg);
                        Logger.i("备份失败："+e.getMessage());
                    }
                    rate += addRate;
                    publishProgress(rate);
                    countDownLatch.countDown();
                }
            });

        }

        private void setEditDataPhoto(ScheduleDetail d, List<String> imagList) {
            StringBuilder content = new StringBuilder();
            List<String> textList = XRichTextStringUtils.cutStringByImgTag(d.getContent());
            int i = 0;
            for (String text : textList) {
                if (text.contains("<img") && text.contains("src=")) {
                    content.append("<img src=\"").append(imagList.get(i++)).append("\"/>");
                } else {
                    content.append(text);
                }
            }
            d.setContent(content.toString());
        }


        private void upDatePhoto(final String cosPath, final String localPath, List<String> photos) {
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
                msg.what=0;
                msg.obj = localPath;
                mHandler.sendMessage(msg);
                isSuccess = false;
                Logger.i("上传失败-----------》" + "\n" + e.getMessage());
            } catch (CosXmlServiceException e) {
                e.printStackTrace();
            }

        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what){
                    case 0:
                        ref.get().showToast(String.format(ref.get().getString(R.string.backup_detail_fial), (String) message.obj));
                        break;
                    case 1:
                        ref.get().showToast((String) message.obj);
                        break;
                }
            }
        };

    }
}
