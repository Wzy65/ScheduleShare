package com.wzy.schedulingshare.base.presenter.impl;

import android.support.annotation.NonNull;

import com.wzy.schedulingshare.base.view.inter.IBaseView;

/**
 * Created by Administrator on 2020/2/22.
 */

public abstract class BasePresenter<V extends IBaseView> {
    protected V mView;

    public BasePresenter(@NonNull V view) {
        attachView(view);
    }

    /**
     * 绑定view，一般在初始化中调用该方法
     *
     * @param view view
     */
    public void attachView(V view) {
        this.mView = view;
    }

    /**
     * 解除绑定view，一般在onDestroy中调用
     */

    public void detachView() {
        this.mView = null;
    }

    /**
     * View是否绑定
     *
     * @return
     */
    public boolean isViewAttached() {
        return mView != null;
    }


}
