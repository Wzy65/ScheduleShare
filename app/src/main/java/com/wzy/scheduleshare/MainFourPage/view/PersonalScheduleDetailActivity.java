package com.wzy.scheduleshare.MainFourPage.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.sendtion.xrichtext.RichTextEditor;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.MainFourPage.presenter.impl.PersonalScheduleDetailPresenterImpl;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.PersonalScheduleDetailPresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.Utils.DateUtils;
import com.wzy.scheduleshare.base.Utils.XRichTextStringUtils;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wzy.scheduleshare.base.view.ScheduleTakePhotosActivity.Schedule_TakePhotoActivity_Intent_Compress_Key;
import static com.wzy.scheduleshare.base.view.ScheduleTakePhotosActivity.Schedule_TakePhotoActivity_Intent_Origin_Key;

/**
 * @ClassName PersonalScheduleDetailActivity
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class PersonalScheduleDetailActivity extends BaseActivity<PersonalScheduleDetailPresenter> implements PersonalScheduleDetailPresenter.View {
    public static final int SCHEDULEDETAIL_REQUEST_TAKEPHOTO = 201;
    public static final String INTENT_TO_PSDA_KEY = "INTENT_TO_PSDA_KEY";
    public static final String INTENT_TO_PSDA_Position_KEY = "INTENT_TO_PSDA_Position_KEY"; //保存选择的位置

    public String schedule_status = "0";  //行程的status，0:未分享，1:已分享

    private Disposable subsLoading;
    private ScheduleDetail mDetail;
    private boolean isCheckContent = true;   //设置标志，从相机相册返回onResume()方法不check内容
    private boolean isSaveTemp = true;
    private int position; //保存前个界面点击所在位置

    @BindView(R.id.share_schedule_detail_goto_comment)
    TextView mShareScheduleDetailGotoComment;
    @BindView(R.id.main_progress)
    ProgressBar mMainProgress;
    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.schedule_detail_title)
    EditText mScheduleDetailTitle;
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
        mPresenter = new PersonalScheduleDetailPresenterImpl(this);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标
        mScheduleDetailContnt.setOnRtImageClickListener(new RichTextEditor.OnRtImageClickListener() {
            @Override
            public void onRtImageClick(View view, String imagePath) {
                showBigPhoto(imagePath);
            }
        });
        position = getIntent().getIntExtra(INTENT_TO_PSDA_Position_KEY, -1);
        mDetail = (ScheduleDetail) getIntent().getSerializableExtra(INTENT_TO_PSDA_KEY);
        if (mDetail != null) {
            isCheckContent = false;  //如果是从列表界面进来，不用去检查Temp表
            isSaveTemp = false;
            schedule_status = mDetail.getStatus();
            invalidateOptionsMenu(); //刷新toolbar栏
            String start = DateUtils.getDateToString(Long.valueOf(mDetail.getStartAt()));
            String end = DateUtils.getDateToString(Long.valueOf(mDetail.getEndAT()));
            showTemp(mDetail.getTitle(), mDetail.getContent(),
                    start.substring(0, 10), start.substring(11, 16), end.substring(0, 10), end.substring(11, 16));
            if (mDetail.getStatus().equals("1")) {
                mShareScheduleDetailGotoComment.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_schedule_datail;
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

        if (isSaveTemp) {
            saveTempDetail();
        }

        /*  rxjava虽然好用，但是总所周知，容易遭层内存泄漏。也就说在订阅了事件后没有及时取阅，
          导致在activity或者fragment销毁后仍然占用着内存，无法释放。而disposable便是这个订阅事件，可以用来取消订阅*/
        if (subsLoading != null && subsLoading.isDisposed()) {
            subsLoading.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSaveTemp) {
            showDialog4onBaskPressed();
        } else {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCHEDULEDETAIL_REQUEST_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    isCheckContent = false;
                    String imageUri_compress = data.getStringExtra(Schedule_TakePhotoActivity_Intent_Compress_Key);
                    String imageUri_origin = data.getStringExtra(Schedule_TakePhotoActivity_Intent_Origin_Key);
                    mScheduleDetailContnt.insertImage(imageUri_compress);
                    Logger.i("原始：" + imageUri_origin + "\n" + "压缩" + imageUri_compress);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_schedule_detail_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isSaveTemp) {
                    showDialog4onBaskPressed();
                } else {
                    finish();
                }
                break;
            case R.id.personal_schedule_detail_menu_save:
                if (checkDate()) {
                    saveDetail(mPresenter.getEditData(mScheduleDetailContnt), true,false);
                }
                break;
            case R.id.personal_schedule_detail_menu_share:
                if (checkDate()) {
                    schedule_status = "1";
                    mPresenter.shareDetail(mScheduleDetailContnt, mDetail, mScheduleDetailTitle.getText().toString(),
                            String.valueOf(DateUtils.getStringToDate(mScheduleDetailStartDate.getText() + " " + mScheduleDetailStartTime.getText())),
                            String.valueOf(DateUtils.getStringToDate(mScheduleDetailEndDate.getText() + " " + mScheduleDetailEndTime.getText())));

                }
                break;
            case R.id.personal_schedule_detail_menu_share_cancel:
                showCancelShareDialog();
        }
        return true;
    }

    /*检查是否选择时间*/
    private boolean checkDate() {
        if (TextUtils.isEmpty(mScheduleDetailStartDate.getText()) || TextUtils.isEmpty(mScheduleDetailStartTime.getText()) ||
                TextUtils.isEmpty(mScheduleDetailEndDate.getText()) || TextUtils.isEmpty(mScheduleDetailEndTime.getText())) {
            showToast(R.string.schedule_detail_noDate_error);
            return false;
        }
        return true;
    }

    /*
    * 动态变换toolbar上的menu控件
    * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        switch (Integer.valueOf(schedule_status)) {
            case 0:
                menu.findItem(R.id.personal_schedule_detail_menu_share).setVisible(true);
                menu.findItem(R.id.personal_schedule_detail_menu_share_cancel).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.personal_schedule_detail_menu_share).setVisible(false);
                menu.findItem(R.id.personal_schedule_detail_menu_share_cancel).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }


    /*保存临时文件*/
    private void saveTempDetail() {
        mPresenter.saveTempDetail(mScheduleDetailTitle.getText().toString(),
                mPresenter.getEditData(mScheduleDetailContnt),
                String.valueOf(DateUtils.getStringToDate(mScheduleDetailStartDate.getText() + " " + mScheduleDetailStartTime.getText())),
                String.valueOf(DateUtils.getStringToDate(mScheduleDetailEndDate.getText() + " " + mScheduleDetailEndTime.getText()))
        );
    }

    @Override
    public void saveDetail(String content, boolean isUpload, boolean isShare) {
        if (mScheduleDetailStartDate.getText().equals(getString(R.string.schedule_detail_chooseDate)) ||
                mScheduleDetailStartTime.getText().equals(getString(R.string.schedule_detail_chooseTime))) {
            showToast(R.string.schedule_detail_noDate_error);
            return;
        }
        mPresenter.saveDetail(mScheduleDetailTitle.getText().toString(),
                content,
                String.valueOf(DateUtils.getStringToDate(mScheduleDetailStartDate.getText() + " " + mScheduleDetailStartTime.getText())),
                String.valueOf(DateUtils.getStringToDate(mScheduleDetailEndDate.getText() + " " + mScheduleDetailEndTime.getText())),
                mDetail, isUpload, isShare
        );
    }

    @Override
    public void finishActivity() {
        isSaveTemp = false;
        mPresenter.deleteTemp();
        finish();
    }

    private void showDialog4ChoosePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalScheduleDetailActivity.this);
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

    /*取消收藏的弹窗*/
    private void showCancelShareDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PersonalScheduleDetailActivity.this);
        builder.setTitle(R.string.schedule_detail_dialog_cancelshare_title);
        builder.setMessage(R.string.schedule_detail_dialog_cancelshare_msg);
        builder.setPositiveButton(R.string.schedule_detail_dialog_cancelshare_right_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                schedule_status = "0";
                mPresenter.cancelShare(mDetail);
                mShareScheduleDetailGotoComment.setVisibility(View.GONE);
                dialog.dismiss();

            }
        });
        builder.setNeutralButton(R.string.schedule_detail_dialog_cancelshare_left_btn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialog4onBaskPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PersonalScheduleDetailActivity.this);
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
                isSaveTemp = false;
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
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
                if (minute < 10) {
                    button.setText(hourOfDay + ":0" + minute);
                } else {
                    button.setText(hourOfDay + ":" + minute);
                }
            }
        }
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE), true).show();
    }

    @OnClick({R.id.schedule_detail_startDate, R.id.schedule_detail_startTime, R.id.schedule_detail_endDate,
            R.id.schedule_detail_endTime, R.id.schedule_detail_choosePhoto, R.id.share_schedule_detail_goto_comment})
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
            case R.id.share_schedule_detail_goto_comment:
                Intent intent = new Intent(PersonalScheduleDetailActivity.this, ShareScheduleDetailActivity.class);
                intent.putExtra(ShareScheduleDetailActivity.INTENT_TO_SSDA_KEY, mDetail);
                startActivity(intent);
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

    @Override
    public String getStatus() {
        return schedule_status;
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
                        if (mScheduleDetailContnt != null) {
                            //在图片全部插入完毕后，再插入一个EditText，防止最后一张图片后无法插入文字
                            mScheduleDetailContnt.addEditTextAtIndex(mScheduleDetailContnt.getLastIndex(), "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(R.string.photo_not_find);
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

    /*
    * 点击小图展示大图
    * */
    private void showBigPhoto(String path) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_show_big, null);
        final AlertDialog dialog = new AlertDialog.Builder(PersonalScheduleDetailActivity.this).create();
        ImageView imageView = (ImageView) view.findViewById(R.id.dialog_show_big_img);
        Glide.with(PersonalScheduleDetailActivity.this).load(path).into(imageView);
        dialog.setView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mMainProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setDetail(ScheduleDetail detail) {
        mDetail = detail;
        Logger.i("设置新的Detail：" + mDetail.getObjectId());
    }

    @OnClick(R.id.share_schedule_detail_goto_comment)
    public void onViewClicked() {
    }
}
