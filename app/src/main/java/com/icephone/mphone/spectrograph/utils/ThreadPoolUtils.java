package com.icephone.mphone.spectrograph.utils;

import com.orhanobut.logger.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by syd on 2016/10/27.
 * 线程池工具
 */

public class ThreadPoolUtils {
    private static ExecutorService mCacheExecutorService;
    private static ExecutorService mFixedExecutorService;

    /**
     * 获取CachedThreadPool
     * @return mCacheExecutorService
     */
    public static ExecutorService getCacheExecutorService(){
        if (mCacheExecutorService==null){
            mCacheExecutorService= Executors.newCachedThreadPool();
        }
        Logger.e("getCacheExecutorService");
        return mCacheExecutorService;
    }
    public static ExecutorService getFixedExecutorService(){
        if (mFixedExecutorService==null){
            mFixedExecutorService= Executors.newFixedThreadPool(1);
        }
        Logger.e("getFixedExecutorService");
        return mFixedExecutorService;
    }





}
