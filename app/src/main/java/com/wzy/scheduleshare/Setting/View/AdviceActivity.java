package com.wzy.scheduleshare.Setting.View;

import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.Setting.modle.Advice;
import com.wzy.scheduleshare.base.modle.User;
import com.wzy.scheduleshare.base.view.impl.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName AdviceActivity
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public class AdviceActivity extends BaseActivity {
    @BindView(R.id.advice_edit)
    EditText mAdviceEdit;
    @BindView(R.id.advice_send)
    Button mAdviceSend;

    @Override
    public void initView() {
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_advice;
    }


    @OnClick(R.id.advice_send)
    public void onViewClicked() {
        if(TextUtils.isEmpty(mAdviceEdit.getText().toString())){
            showToast(R.string.advice_error);
        }else {
            Advice advice=new Advice();
            advice.setUser(BmobUser.getCurrentUser(User.class));
            advice.setContent(mAdviceEdit.getText().toString());
            advice.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        showToast(R.string.advice_send_success);
                        finish();
                    }else {
                        showToast(R.string.advice_send_fail);
                        Logger.i("留言发送失败"+e.getMessage());
                    }
                }
            });
        }
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
