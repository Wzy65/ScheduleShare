package com.wzy.schedulingshare.LoginAndRegister.presenter.inter;

import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

import cn.bmob.v3.exception.BmobException;

/**
 * @ClassName RegisterPresenter
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public interface RegisterPresenter extends IBasePresenter {
    interface View {
        void attemptRegister();

        void showProgress(boolean flag);

        void onFailure(BmobException e);
    }

    boolean isUserIdValid(String userId);

    boolean isPasswordValid(String password);

    boolean isPasswordConfirmValid(String password1, String password2);

    void signUp(String username, String password);
}
