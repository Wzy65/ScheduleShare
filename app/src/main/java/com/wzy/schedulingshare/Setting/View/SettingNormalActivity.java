package com.wzy.schedulingshare.Setting.View;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.Setting.presenter.impl.BaseSettingPresenterImpl;
import com.wzy.schedulingshare.Setting.presenter.inter.BaseSettingPresenter;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingNormalActivity extends BaseActivity<BaseSettingPresenter> {

    public static final String SETTING_NORMAL_TOOLBAR_TITLE="SETTING_NORMAL_TOOLBAR_TITLE";
    public static final String SETTING_NORMAL_EDIT_HINT="SETTING_NORMAL_EDIT_HINT";
    public static final String SETTING_NORMAL_EDIT_ERROR="SETTING_NORMAL_EDIT_ERROR";
    public static final String SETTING_NORMAL_TYPE="SETTING_NORMAL_TYPE";

    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.setting_normal_edit)
    EditText mSettingNormalEdit;
    @BindView(R.id.setting_ok_button)
    Button mSettingOkButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_normal;
    }


    @Override
    public void initView() {
        mPresenter = new BaseSettingPresenterImpl(this);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标
        mSettingToolbar.setTitle(getIntent().getStringExtra(SETTING_NORMAL_TOOLBAR_TITLE));
        mSettingNormalEdit.setHint(getIntent().getStringExtra(SETTING_NORMAL_EDIT_HINT));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }


    @OnClick(R.id.setting_ok_button)
    public void onViewClicked() {
        int type=getIntent().getIntExtra(SETTING_NORMAL_TYPE,-1);
        String str=mSettingNormalEdit.getText().toString();
        if(!mPresenter.isValueValid(type,str)){
            mSettingNormalEdit.setError(getIntent().getStringExtra(SETTING_NORMAL_EDIT_ERROR));
        }else {
            mPresenter.update(type,str);
        }
    }
}
