package com.wzy.scheduleshare.Setting.presenter.inter;

import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;

/**
 * @ClassName MainFourPagePresenter
 * @Author Wei Zhouye
 * @Date 2020/2/23
 * @Version 1.0
 */
public interface SettingPresenter extends IBasePresenter {
    interface View {
        void updateSexy(String str);

        void updateHeadIcon(String path);
    }

    void openSysAlbum();

    void openSysCamera();

    void uploadHeadIcon(String path);

    void updateSexy(String str);

    String getLocalHeadIcon();
}
