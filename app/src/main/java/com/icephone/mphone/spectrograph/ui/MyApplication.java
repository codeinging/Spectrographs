package com.icephone.mphone.spectrograph.ui;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.Logger;

/**
 * Created by syd on 2016/10/14.
 */

public class MyApplication extends Application {
    public static boolean isDebug;
    public static String APP_NAME = "Spectrograph";
//    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Fresco
        Logger.d("初始化Fresco");
        Fresco.initialize(this);
    }

    /*@Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }*/
    /*public static Context getContext(){
        return context;
    }*/

}
