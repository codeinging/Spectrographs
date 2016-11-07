package com.icephone.mphone.spectrograph.presenter;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by syd on 2016/10/10.
 */

public abstract class BasePresenter<T> {
    //注意这里使用弱引用，避免造成没存泄露
    protected Reference<T> mViewRef;

    /**
     * 建立关联
     *
     * @param view
     */
    public void attachView(T view) {
        mViewRef = new WeakReference<T>(view);
    }
    /**
     * 获取View
     *
     * @return T
     */
    protected T getView() {
        return mViewRef.get();
    }

    /**
     * 检测是否已经和View建立关联
     *
     * @return boolean
     */
    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /**
     * 接触view绑定
     */
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        cancelTask();//解除绑定的同时停止任务

    }

    /**
     * 解除绑定的同时停止任务
     */
    public abstract void cancelTask();

}
