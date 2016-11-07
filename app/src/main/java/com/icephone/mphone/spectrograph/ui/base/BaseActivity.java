package com.icephone.mphone.spectrograph.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.icephone.mphone.spectrograph.ui.actiivty.LoginActivity;
import com.icephone.mphone.spectrograph.ui.actiivty.PermissionsActivity;
import com.icephone.mphone.spectrograph.utils.PermissionsChecker;
import com.icephone.mphone.spectrograph.utils.TempMessageManager;
import com.orhanobut.logger.Logger;

/**
 * Created by syd on 2016/10/16.
 */

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 是否需要登录
     */
    protected boolean isNeedLogin=false;
    protected final int PERMISSION_REQUESTCODE =0x122;
    private final String TAG=BaseActivity.class.getSimpleName();
    protected static final int PERMISSIONS_GRANTED = 0; // 权限授权
    protected static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register2AppManager();
        Logger.init(TAG);

        /**
         * 是否需要登录，如果需要登录的话会进入LoginActivity
         */
        if (isNeedLogin && TempMessageManager.getUserInfo(this) == null) {
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
            return;
        }
        initData(savedInstanceState);
        initView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        String[] allneedPermissions=getNeededPermissions();

        if (allneedPermissions!=null&& PermissionsChecker.lacksPermissions(this,allneedPermissions)) {
            startPermissionsActivity(this,allneedPermissions);
        }
    }

    /**
     * 设定所需权限，无需权限可以返回null
     *
     * @return
     */
    protected String[] getNeededPermissions() {
        return null;
    }

    /**
     * 加如Activity栈
     */
    protected abstract void register2AppManager();


    /**
     * 初始化数据，
     * 包括配置改变（如横竖屏切换）时的数据恢复
     * 在initView之前被调用
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);


    /**
     * 初始化布局和View,
     * 在初始化数据initData之后被调用
     */
    protected abstract void initView();
    /**
     * 设置是否需要登录
     *
     * @param isneed
     */
    protected void isNeedLogin(boolean isneed) {
        this.isNeedLogin = isneed;
    }
    protected void startPermissionsActivity(Activity context, String[]permissisons) {
        PermissionsActivity.startActivityForResult(context, PERMISSION_REQUESTCODE, permissisons);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String[] permissions=data.getExtras().getStringArray("permissions");
        if (requestCode==PERMISSION_REQUESTCODE&&permissions!=null) {
            if (resultCode==PERMISSIONS_GRANTED) {
                allPermissionGranted(permissions);
            }else {
                hasPermissionDenied();
            }
        }
    }

    /**
     *
     * 申请权限失败
     */
    protected  void hasPermissionDenied(){

    }

    /**
     * 所有需要的权限被允许的回调
     * @param permissions
     */
    protected  void allPermissionGranted(String[] permissions){

    }

}
