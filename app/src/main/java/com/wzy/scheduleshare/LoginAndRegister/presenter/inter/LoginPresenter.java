package com.wzy.scheduleshare.LoginAndRegister.presenter.inter;

import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName LoginPresenter
 * @Author Wei Zhouye
 * @Date 2020/2/22
 * @Version 1.0
 */
public interface LoginPresenter extends IBasePresenter {
    interface View {
        void showProgress(boolean flag);

        void setError(int type);  //用户id和密码的错误显示

        void Login();
    }

    void attemptLogin();

    void populateAutoComplete();

    void login(String usename, String password);
}
