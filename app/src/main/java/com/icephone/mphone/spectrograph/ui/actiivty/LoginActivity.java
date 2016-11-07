package com.icephone.mphone.spectrograph.ui.actiivty;

import android.net.Uri;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;
import com.icephone.mphone.spectrograph.R;
import com.icephone.mphone.spectrograph.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by syd on 2016/10/17.
 */
public class LoginActivity extends BaseActivity {
    @Bind(R.id.img)
    SimpleDraweeView img;

    /**
     * 加如Activity栈
     */
    @Override
    protected void register2AppManager() {

    }

    /**
     * 初始化数据，
     * 包括配置改变（如横竖屏切换）时的数据恢复
     * 在initData之前被调用
     *
     * @param savedInstanceState
     */
    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    /**
     * 初始化布局和View,
     * 在初始化数据initData之后被调用
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Uri uri = Uri.parse("https://raw.githubusercontent.com/facebook/fresco/gh-pages/static/logo.png");
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.img);
        draweeView.setImageURI(uri);

    }
}
