package com.icephone.mphone.spectrograph.ui.actiivty;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.icephone.mphone.spectrograph.presenter.Impl.BookPresenter;
import com.icephone.mphone.spectrograph.ui.base.MvpBaseActivity;
import com.icephone.mphone.spectrograph.ui.view.BookView;

/**
 * Created by syd on 2016/10/10.
 */

public class TestActivity extends MvpBaseActivity<BookView,BookPresenter>implements BookView{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();
//        initViews();
//        BasePresenter basePresenter=creatPresenter();
        mPresenter.fech();
    }

    /**
     * 建立Presenter
     *
     * @return P
     */
    @Override
    protected BookPresenter creatPresenter() {
        return null;
    }
}
