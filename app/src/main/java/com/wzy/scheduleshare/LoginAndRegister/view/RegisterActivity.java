package com.wzy.scheduleshare.LoginAndRegister.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wzy.scheduleshare.LoginAndRegister.presenter.impl.RegisterPresnterImpl;
import com.wzy.scheduleshare.LoginAndRegister.presenter.inter.RegisterPresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;

/**
 * @ClassName RegisterActivity
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterPresenter.View {


    @BindView(R.id.register_progress)
    ProgressBar mRegisterProgress;
    @BindView(R.id.userId)
    AutoCompleteTextView mUserId;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.password_confirm)
    EditText mPasswordConfirm;
    @BindView(R.id.register_button)
    Button mRegisterButton;
    @BindView(R.id.btn_backto_login)
    Button mBtnBacktoLogin;
    @BindView(R.id.email_register_form)
    LinearLayout mEmailRegisterForm;
    @BindView(R.id.register_form)
    ScrollView mRegisterForm;


    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mPresenter=new RegisterPresnterImpl(this);
        mPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }


    @OnClick({R.id.register_button, R.id.btn_backto_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                attemptRegister();
                break;
            case R.id.btn_backto_login:
                finish();
                break;
        }
    }

    @Override
    public void attemptRegister() {
        // Reset errors.
        mUserId.setError(null);
        mPassword.setError(null);
        mPasswordConfirm.setError(null);

        // Store values at the time of the register attempt.
        String userId = mUserId.getText().toString();
        String password = mPassword.getText().toString();
        String password_confirm = mPasswordConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid userId address.
        if (TextUtils.isEmpty(userId)) {
            mUserId.setError(getString(R.string.error_field_required));
            focusView = mUserId;
            cancel = true;
        } else if (!mPresenter.isUserIdValid(userId)) {
            mUserId.setError(getString(R.string.error_register_phonenumber));
            focusView = mUserId;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (!mPresenter.isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(password_confirm)) {
            mPasswordConfirm.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirm;
            cancel = true;
        } else if (!mPresenter.isPasswordConfirmValid(password, password_confirm)) {
            mPasswordConfirm.setError(getString(R.string.error_incorrect_password_confirm));
            focusView = mPasswordConfirm;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mPresenter.signUp(userId,password);
        }
    }

    /**
     * Shows the progress UI and hides the Register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onFailure(BmobException e) {
        showToast(e.getMessage());
    }


}
