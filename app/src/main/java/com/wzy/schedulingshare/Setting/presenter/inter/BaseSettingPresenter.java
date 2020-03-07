package com.wzy.schedulingshare.Setting.presenter.inter;

import com.wzy.schedulingshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName MainFourPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public interface BaseSettingPresenter extends IBasePresenter {
    void update(int type, String str);

    void updatePassword(String old, String newpw);

    boolean isValueValid(int type, String str);
}
