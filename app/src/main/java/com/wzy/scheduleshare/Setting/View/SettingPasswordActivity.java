package com.wzy.scheduleshare.Setting.View;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.Setting.presenter.impl.BaseSettingPresenterImpl;
import com.wzy.scheduleshare.Setting.presenter.inter.BaseSettingPresenter;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingPasswordActivity extends BaseActivity<BaseSettingPresenter> {

    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.setting_pw_original)
    EditText mSettingPwOriginal;
    @BindView(R.id.setting_pw_new)
    EditText mSettingPwNew;
    @BindView(R.id.setting_pw_confirm)
    EditText mSettingPwConfirm;
    @BindView(R.id.setting_ok_button)
    Button mRegisterButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_password;
    }


    @Override
    public void initView() {
        mPresenter=new BaseSettingPresenterImpl(this) ;
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标
        mSettingToolbar.setTitle(getString(R.string.setting_changepw));
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
        // Store values at the time of the register attempt.
        String origin = mSettingPwOriginal.getText().toString();
        String newpw = mSettingPwNew.getText().toString();
        String confirm = mSettingPwConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(origin)){
            mSettingPwOriginal.setError(getString(R.string.error_field_required));
            focusView=mSettingPwOriginal;
            cancel=true;
        } else if (TextUtils.isEmpty(newpw)) {
            mSettingPwNew.setError(getString(R.string.error_field_required));
            focusView = mSettingPwNew;
            cancel = true;
        } else if (newpw.length()<6) {
            mSettingPwNew.setError(getString(R.string.setting_pw_error_new));
            focusView = mSettingPwNew;
            cancel = true;
        } else if (TextUtils.isEmpty(confirm)) {
            mSettingPwConfirm.setError(getString(R.string.error_field_required));
            focusView = mSettingPwConfirm;
            cancel = true;
        } else if (!confirm.equals(newpw)) {
            mSettingPwConfirm.setError(getString(R.string.setting_pw_error_confirm));
            focusView = mSettingPwConfirm;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mPresenter.updatePassword(origin,newpw);
        }
    }
}
