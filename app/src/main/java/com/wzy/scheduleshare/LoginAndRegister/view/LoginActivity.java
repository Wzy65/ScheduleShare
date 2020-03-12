package com.wzy.scheduleshare.LoginAndRegister.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.wzy.scheduleshare.LoginAndRegister.presenter.impl.LoginPresenterImpl;
import com.wzy.scheduleshare.LoginAndRegister.presenter.inter.LoginPresenter;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName LoginActivity
 * @Author Wei Zhouye
 * @Date 2020/2/22
 * @Version 1.0
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginPresenter.View {

    public static final int USERID_ERROR = 0;
    public static final int PASSWORD_ERROR = 1;
    public static final String NOT_AUTO_LOGIN="not_auto_login";

    @BindView(R.id.login_layout)
    LinearLayout mLoginLayout;
    @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;
    @BindView(R.id.userId)
    AutoCompleteTextView mUserId;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.sign_in_button)
    Button mSignInButton;
    @BindView(R.id.btn_goto_register)
    Button mBtnGotoRegister;
    @BindView(R.id.login_form)
    ScrollView mLoginForm;


    @Override
    protected void onResume() {
        super.onResume();
        mLoginLayout.requestFocus();
        mUserId.setText("");
        mPassword.setText("");
        mUserId.setError(null);
        mPassword.setError(null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //检查权限
        checkStoragePermissions(0);

        mPresenter = new LoginPresenterImpl(this);
        if(TextUtils.isEmpty(getIntent().getStringExtra(NOT_AUTO_LOGIN))) {
            mPresenter.attemptLogin();
        }
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NONE || id == EditorInfo.IME_NULL) {
                    Login();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.sign_in_button, R.id.btn_goto_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                Login();
                break;
            case R.id.btn_goto_register:
                mLoginForm.requestFocus();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void setError(int type) {
        switch (type) {
            case USERID_ERROR:
                mUserId.setError(getString(R.string.error_invalid_userId));
                mUserId.requestFocus();
                break;
            case PASSWORD_ERROR:
                mPassword.setError(getString(R.string.error_incorrect_password));
                mPassword.requestFocus();
                break;
        }
    }

    @Override
    public void Login() {
        // Reset errors.
        mUserId.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String UserId = mUserId.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid UserId address.
        if (TextUtils.isEmpty(UserId)) {
            mUserId.setError(getString(R.string.error_field_required));
            focusView = mUserId;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mPresenter.login(UserId, password);
            mLoginLayout.requestFocus();
        }
    }


    /**
     * 兼容android6.0运行时权限
     * 检查权限
     *
     * @param requestCode
     */
    public void checkStoragePermissions(int requestCode) {
        List<String> permissions = new ArrayList<>();
        int permissionCheckWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        int permissionCheckRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissions.size() > 0) {
            String[] missions = new String[]{};
            ActivityCompat.requestPermissions(this, permissions.toArray(missions), requestCode);
        } else {

        }
    }

    /**
     * 兼容android6.0运行时权限
     * <p>
     * 权限授权结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
