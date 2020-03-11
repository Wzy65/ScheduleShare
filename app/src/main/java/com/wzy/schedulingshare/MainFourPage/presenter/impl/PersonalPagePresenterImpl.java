package com.wzy.schedulingshare.MainFourPage.presenter.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.PersonalPagePresenter;
import com.wzy.schedulingshare.MainFourPage.view.PersonalDetailFragment;
import com.wzy.schedulingshare.base.Utils.DBUtils;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.presenter.impl.BasePresenter;
import com.wzy.schedulingshare.base.view.impl.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName PersonalPagePresenterImpl
 * @Author Wei Zhouye
 * @Date 2020/3/8
 * @Version 1.0
 */
public class PersonalPagePresenterImpl extends BasePresenter<PersonalDetailFragment> implements PersonalPagePresenter {

    public PersonalPagePresenterImpl(@NonNull PersonalDetailFragment view) {
        super(view);
    }

    @Override
    public void querySchedule() {
        new QueryTask(mView).execute((Void) null);
    }

    @Override
    public void deleteDetail(String createTime) {
        DBUtils.getINSTANCE(mView.getContext()).deleteDetail(createTime);
    }

    @Override
    public void deleteDetailInBmob(String id, UpdateListener listener) {
        if (!TextUtils.isEmpty(id)) ;
        ScheduleDetail detail = new ScheduleDetail();
        detail.delete(id,listener);
    }

    @Override
    public List<ScheduleDetail> queryScheduleShare(List<ScheduleDetail> details) {
        if (details == null) {
            return null;
        }
        List<ScheduleDetail> temp = new ArrayList<>();
        for (ScheduleDetail detail : details) {
            if (detail.getStatus().equals("1")) {
                temp.add(detail);
            }
        }
        return temp;
    }


    public static class QueryTask extends AsyncTask<Void, Void, List<ScheduleDetail>> {

        private WeakReference<PersonalDetailFragment> ref;

        public QueryTask(PersonalDetailFragment fragment) {
            if (fragment != null) {
                ref = new WeakReference<PersonalDetailFragment>(fragment);
            }
        }

        @Override
        protected List<ScheduleDetail> doInBackground(Void... params) {
            if (ref == null) {
                return null;
            }
            return DBUtils.getINSTANCE(ref.get().getContext()).getAllDetail(BmobUser.getCurrentUser(User.class).getObjectId());
        }

        @Override
        protected void onPostExecute(final List<ScheduleDetail> list) {
            if (ref.get() == null) {
                return;
            }
            ref.get().setAllList(list);
            ref.get().refreshScheduleList(list);
        }

    }
}
