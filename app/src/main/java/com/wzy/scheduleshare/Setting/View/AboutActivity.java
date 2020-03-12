package com.wzy.scheduleshare.Setting.View;

import android.view.MenuItem;

import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

/**
 * @ClassName AboutActivity
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public class AboutActivity extends BaseActivity{

    @Override
    public void initView() {
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
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
}
