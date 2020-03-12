package com.wzy.scheduleshare.base.view.inter;

/**
 * Created by Administrator on 2020/2/22.
 */

public interface IBaseView {

    void initView();

    /**
     * 显示加载中
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 数据获取失败
     *
     * @param throwable
     */
    void onError(Throwable throwable);

    /**
     * 显示提示
     *
     * @param msgId
     */
    void showToast(int msgId);

    void showToast(String msg);

}
