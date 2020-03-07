package com.wzy.schedulingshare.MainFourPage.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.orhanobut.logger.Logger;
import com.sendtion.xrichtext.RichTextEditor;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.ScheduleDetailPresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.ScheduleDetailPresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.Utils.DateUtils;
import com.wzy.schedulingshare.base.Utils.XRichTextStringUtils;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wzy.schedulingshare.base.view.ScheduleTakePhotosActivity.Schedule_TakePhotoActivity_Intent_Compress_Key;
import static com.wzy.schedulingshare.base.view.ScheduleTakePhotosActivity.Schedule_TakePhotoActivity_Intent_Origin_Key;

/**
 * @ClassName ScheduleDetailActivity
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class ScheduleDetailActivity extends BaseActivity<ScheduleDetailPresenter> implements ScheduleDetailPresenter.View {

    public static final int SCHEDULEDETAIL_REQUEST_TAKEPHOTO = 201;
    private Disposable subsLoading;
    private boolean isCheckContent = true;   //设置标志，从相机相册返回onResume()方法不check内容

    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.schedule_detail_title)
    EditText mScheduleDetailTitle;
    @BindView(R.id.schedule_detail_userIcon)
    ImageView mScheduleDetailUserIcon;
    @BindView(R.id.schedule_detail_uerName)
    TextView mScheduleDetailUerName;
    @BindView(R.id.schedule_detail_updateAt)
    TextView mScheduleDetailUpdateAt;
    @BindView(R.id.schedule_detail_userInfo)
    LinearLayout mScheduleDetailUserInfo;
    @BindView(R.id.schedule_detail_startDate)
    Button mScheduleDetailStartDate;
    @BindView(R.id.schedule_detail_startTime)
    Button mScheduleDetailStartTime;
    @BindView(R.id.schedule_detail_endDate)
    Button mScheduleDetailEndDate;
    @BindView(R.id.schedule_detail_endTime)
    Button mScheduleDetailEndTime;
    @BindView(R.id.schedule_detail_choosePhoto)
    ImageView mScheduleDetailChoosePhoto;
    @BindView(R.id.schedule_detail_contnt)
    RichTextEditor mScheduleDetailContnt;

    @Override
    public void initView() {
        mPresenter = new ScheduleDetailPresenterImpl(this);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_schedule_datail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCheckContent) {
            mPresenter.checkTemp();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.saveTempDetail(mScheduleDetailTitle.getText().toString(),
                mPresenter.getEditData(mScheduleDetailContnt),
                String.valueOf(DateUtils.getStringToDate(mScheduleDetailStartDate.getText() + " " + mScheduleDetailStartTime.getText())),
                String.valueOf(DateUtils.getStringToDate(mScheduleDetailEndDate.getText() + " " + mScheduleDetailEndTime.getText()))
        );
        /*  rxjava虽然好用，但是总所周知，容易遭层内存泄漏。也就说在订阅了事件后没有及时取阅，
          导致在activity或者fragment销毁后仍然占用着内存，无法释放。而disposable便是这个订阅事件，可以用来取消订阅*/
        if (subsLoading != null && subsLoading.isDisposed()) {
            subsLoading.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        showDialog4onBaskPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCHEDULEDETAIL_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    isCheckContent=false;
                    String imageUri_compress = data.getStringExtra(Schedule_TakePhotoActivity_Intent_Compress_Key);
                    String imageUri_origin = data.getStringExtra(Schedule_TakePhotoActivity_Intent_Origin_Key);
                    mScheduleDetailContnt.insertImage(imageUri_compress);
                    Logger.i("原始：" + imageUri_origin + "\n" + "压缩" + imageUri_compress);
                    //TODO
                    //mPresenter.uploadHeadIcon(imageUri);
                    //if(BmobUser.getCurrentUser(User.class).getHeadIcon()!=null) {
                    //   Glide.with(this).load(new File(imageUri)).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(mSettingHeadIconImg);
                    //}
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_detail_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showDialog4onBaskPressed();
                break;
            case R.id.schedule_detail_menu_save:
                break;

        }
        return true;
    }

    private void showDialog4ChoosePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleDetailActivity.this);
        builder.setTitle(getString(R.string.schedule_detail_dialog_choosePhoto_title));
        String[] choice = {getString(R.string.schedule_detail_dialog_choosePhoto_camera), getString(R.string.schedule_detail_dialog_choosePhoto_album)};
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mPresenter.openSysCamera();
                        break;
                    case 1:
                        mPresenter.openSysAlbum();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialog4onBaskPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleDetailActivity.this);
        builder.setTitle(R.string.schedule_detail_dialog_Back_title);
        builder.setMessage(R.string.schedule_detail_dialog_Back_msg);
        builder.setPositiveButton(R.string.schedule_detail_dialog_Back_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNeutralButton(R.string.schedule_detail_dialog_Back_left_btn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void showDateChoose(final Button button) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                button.setText(year + "-" + month + "-" + dayOfMonth);
            }
        }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeChoose(final Button button) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                button.setText(hourOfDay + ":" + minute);
            }
        }
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), true).show();
    }

    @OnClick({R.id.schedule_detail_startDate, R.id.schedule_detail_startTime, R.id.schedule_detail_endDate, R.id.schedule_detail_endTime, R.id.schedule_detail_choosePhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.schedule_detail_startDate:
                showDateChoose(mScheduleDetailStartDate);
                break;
            case R.id.schedule_detail_startTime:
                showTimeChoose(mScheduleDetailStartTime);
                break;
            case R.id.schedule_detail_endDate:
                showDateChoose(mScheduleDetailEndDate);
                break;
            case R.id.schedule_detail_endTime:
                showTimeChoose(mScheduleDetailEndTime);
                break;
            case R.id.schedule_detail_choosePhoto:
                showDialog4ChoosePhoto();
                break;
        }
    }

    @Override
    public void showTemp(String title, final String content, String startDate, String startTime, String endDate, String endTime) {
        mScheduleDetailTitle.setText(title);
        mScheduleDetailStartDate.setText(startDate);
        mScheduleDetailStartTime.setText(startTime);
        mScheduleDetailEndDate.setText(endDate);
        mScheduleDetailEndTime.setText(endTime);
        mScheduleDetailContnt.post(new Runnable() {
            @Override
            public void run() {
                dealWithContent(content);
            }
        });
    }

    private void dealWithContent(String content) {
        mScheduleDetailContnt.clearAllLayout();
        showDataSync(content);
    }

    /**
     * 异步方式显示数据
     */
    private void showDataSync(final String html) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                showEditData(emitter, html);
            }
        })
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {
                       /* if (loadingDialog != null){
                            loadingDialog.dismiss();
                        }*/
                        if (mScheduleDetailContnt != null) {
                            //在图片全部插入完毕后，再插入一个EditText，防止最后一张图片后无法插入文字
                            mScheduleDetailContnt.addEditTextAtIndex(mScheduleDetailContnt.getLastIndex(), "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                       /* if (loadingDialog != null){
                            loadingDialog.dismiss();
                        }*/
                        showToast("解析错误：图片不存在或已损坏");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subsLoading = d;
                    }

                    @Override
                    public void onNext(String text) {
                        try {
                            if (mScheduleDetailContnt != null) {
                                if (text.contains("<img") && text.contains("src=")) {
                                    //imagePath可能是本地路径，也可能是网络地址
                                    String imagePath = XRichTextStringUtils.getImgSrc(text);
                                    //Log.e("---", "###imagePath=" + imagePath);
                                    //插入空的EditText，以便在图片前后插入文字
                                    mScheduleDetailContnt.addEditTextAtIndex(mScheduleDetailContnt.getLastIndex(), "");
                                    mScheduleDetailContnt.addImageViewAtIndex(mScheduleDetailContnt.getLastIndex(), imagePath);
                                } else {
                                    mScheduleDetailContnt.addEditTextAtIndex(mScheduleDetailContnt.getLastIndex(), text);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 显示数据
     */
    protected void showEditData(ObservableEmitter<String> emitter, String html) {
        try {
            List<String> textList = XRichTextStringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
            emitter.onError(e);
        }
    }

}
