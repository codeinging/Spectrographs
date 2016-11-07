package com.icephone.mphone.spectrograph.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.icephone.mphone.spectrograph.presenter.BasePresenter;

/**
 * Created by syd on 2016/10/10.
 */

public abstract class MvpBaseActivity<V,P extends BasePresenter<V>> extends AppCompatActivity {
    protected P mPresenter;//Presenter对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter= creatPresenter();//建立Presenter
        mPresenter.attachView((V) this);//View与Presenter建立关联
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();//当View生命周期结束销毁时，解除关联
        //注意：View结束并不一定会执行这里，这就是在BasePresenter
        //使用弱引用的原因
    }

    /**
     * 建立Presenter
     * @return P
     */
    protected abstract P creatPresenter();
}
