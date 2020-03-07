package com.wzy.schedulingshare.base.Utils.BmobUtils;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;

/**
 * @ClassName UpdateCacheListener
 * @Author Wei Zhouye
 * @Date 2020/3/5
 * @Version 1.0
 */
public abstract class UpdateCacheListener extends BmobListener1 {
    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}