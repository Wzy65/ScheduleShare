package com.wzy.scheduleshare.base.view;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;
import com.wzy.scheduleshare.base.presenter.impl.SplashPresenterImpl;
import com.wzy.scheduleshare.base.presenter.inter.SpashPresenter;

/**
 * @ClassName SplashActivity
 * @Author Wei Zhouye
 * @Date 2020/2/29
 * @Version 1.0
 */
public class SplashActivity extends BaseActivity<SpashPresenter> implements  SpashPresenter.View {




    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
       mPresenter=new SplashPresenterImpl(this);
        mPresenter.start();
    }


}
