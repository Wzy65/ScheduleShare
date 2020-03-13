package com.wzy.scheduleshare.MainFourPage.presenter.inter;

import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName MainFourPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public interface MainFourPagePresenter extends IBasePresenter {
    interface View{
        void showProgress(final boolean show);

        void setProgressRate(int value); //更新进度条消息
    }

    boolean searchNewFriend(String phoneNumber);

    String getLocalHeadIcon();

    void clearTCKey();

    void backUp();  //备份数据

    void recovery(); //恢复数据
}
