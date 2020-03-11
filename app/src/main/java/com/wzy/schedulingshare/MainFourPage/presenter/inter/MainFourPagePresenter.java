package com.wzy.schedulingshare.MainFourPage.presenter.inter;

import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName MainFourPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public interface MainFourPagePresenter extends IBasePresenter {
    interface View{
        void showProgress(final boolean show);
    }

    boolean searchNewFriend(String phoneNumber);

    String getLocalHeadIcon();

    void clearTCKey();
}
