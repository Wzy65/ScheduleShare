package com.wzy.scheduleshare.base.view.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import android.widget.Toast;

import com.orhanobut.logger.Logger;

import com.wzy.scheduleshare.base.presenter.inter.IBasePresenter;
import com.wzy.scheduleshare.base.view.inter.IBaseView;

import butterknife.Unbinder;


/**
 * @ClassName BaseFragment
 * @Author Wei Zhouye
 * @Date 2020/3/2
 * @Version 1.0
 */
public abstract class BaseFragment<P extends IBasePresenter> extends Fragment implements IBaseView {

    protected P mPresenter;

    protected Unbinder unbinder;

    protected abstract int getLayoutId();

    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "----->onCteate");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(getClass().getSimpleName(), "------------>onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(getClass().getSimpleName(), "------------>onDetach");
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(getClass().getSimpleName(), "------------>onDestroy");
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void showToast(int msgId) {
        //分3行写的目的是避免小米手机前面显示应用名
        Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        toast.setText(getString(msgId));
        toast.show();
        Logger.i(getString(msgId));
    }

    @Override
    public void showToast(String msg) {
        //分3行写的目的是避免小米手机前面显示应用名
        Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
        Logger.i(msg);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
