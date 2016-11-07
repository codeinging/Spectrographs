package com.icephone.mphone.spectrograph.utils;

import android.app.Activity;
import android.app.Application;

import com.orhanobut.logger.Logger;

import java.util.Stack;

/**
 * Created by syd on 2016/10/13.
 * activity堆栈式管理
 */
public class AppManager {
    private String TAG=AppManager.class.getSimpleName();
    private static AppManager instance;
    private static Stack<Activity> mActivityStack;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }
    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }
    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        return mActivityStack.lastElement();
    }
    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (mActivityStack!=null) {
            Activity activity=mActivityStack.lastElement();
            if (activity!=null) {
                finishActivity(activity);
                activity=null;
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            if (mActivityStack!=null&&mActivityStack.contains(activity)) {
                mActivityStack.remove(activity);
            }
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (mActivityStack!=null) {
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (mActivityStack!=null) {
            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
                if (null != mActivityStack.get(i)) {
                    finishActivity(mActivityStack.get(i));
                    break;
                }
            }
            mActivityStack.clear();
        }
    }

    /**
     * 获取指定的Activity
     */
    public static Activity getActivity(Class<?> cls) {
        if (mActivityStack != null)
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    public static Stack<Activity> getActivitys() {
        if (mActivityStack!=null) {
            return mActivityStack;
        }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Application context) {
        Logger.d("AppExit");
        try {
            finishAllActivity();
            context.onTerminate();
        } catch (Exception ignored) {
            Logger.e(ignored);
        }
    }

    /**
     * 返回当前Activity栈中Activity的数量
     *
     * @return
     */
    public int getActivityCount() {
        if (mActivityStack!=null){
            return mActivityStack.size();
        }
        return 0;
    }

    /**
     * 堆栈中移除Activity
     */
    public void removeActivity(Activity activity) {
        if (mActivityStack == null) {
            return;
        } else if (mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
        }

        if (activity != null && !activity.isFinishing()) {
            activity.finish();
            activity = null;
        }
    }
}
