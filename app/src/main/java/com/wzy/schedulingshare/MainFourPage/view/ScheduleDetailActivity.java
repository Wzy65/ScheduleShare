package com.wzy.schedulingshare.MainFourPage.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.wzy.schedulingshare.MainFourPage.presenter.impl.ScheduleDetailPresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.ScheduleDetailPresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.Setting.View.SettingActivity;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wzy.schedulingshare.Setting.View.SettingActivity.REQUEST_HEADICON;
import static com.wzy.schedulingshare.base.view.TakePhotosNotCropActivity.TakePhotoNotCropActivity_Intent_Key;

/**
 * @ClassName ScheduleDetailActivity
 * @Author Wei Zhouye
 * @Date 2020/3/7
 * @Version 1.0
 */
public class ScheduleDetailActivity extends BaseActivity<ScheduleDetailPresenter> implements ScheduleDetailPresenter.View {
    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;

    @Override
    public void initView() {
        mPresenter=new ScheduleDetailPresenterImpl(this);
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
    public void onBackPressed(){
        showDialog4onBaskPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_HEADICON:
                if (resultCode == RESULT_OK) {
                    String imageUri = data.getStringExtra(TakePhotoNotCropActivity_Intent_Key);
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
                finish();
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
        alertDialog.setCanceledOnTouchOutside(false);
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
}
