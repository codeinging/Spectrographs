package com.icephone.mphone.spectrograph.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.icephone.mphone.spectrograph.utils.okhttphelpr.OkHttpHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by syd on 2016/10/13.
 * 未捕获异常处理类
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //系统默认的UncaughtExceptionHandler
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //context
    private Context mApplicationContext;
    private Application application;
    //存储异常和参数信息
    private static Map<String, String> paramMap;
    //Tag
    private final String TAG = CrashHandler.class.getSimpleName();
    //时间格式
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd-hh-mm-ss", Locale.CHINA);
    //AndroidStudio在这里报出内存泄漏的警告，
    // 不过用AppliCationContext就可以解决了
    private static CrashHandler mInstance;

    //所需权限
    private final String[] all_need_permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //上传crash文件服务器url
    private String mCrashUrl = null;
    //上传crash文件表单文件项名
    private String name = null;

    /**
     * 构造函数私有化实现单例模式
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler单例
     *
     * @return
     */
    public static CrashHandler getInstance() {
        if (mInstance == null) {
            mInstance = new CrashHandler();
            paramMap=new HashMap<>();
        }
        return mInstance;
    }

    /**
     * 初始化操作，必须要进行
     *
     * @param application
     * @param callBack 回调接口
     */
    public void init(Application application, CallBack callBack) {
        this.mApplicationContext =application.getApplicationContext();
        this.application=application;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置我们的CrashHandler为系统默认的UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(this);
        //android 6.0以上动态申请权限
        //判断权限是否满足
        callBack.needPermissions(all_need_permissions);
    }

    /**
     * 含有crash文件上传初始化操作，必须要进行
     *
     * @param context
     * @param crashUrl Crash文件上传地址
     * @param name     上传crash文件表单文件项名
     * @param callBack 回调接口
     */
    public void init(Context context, String crashUrl, String name, CallBack callBack) {
        this.mApplicationContext = context.getApplicationContext();
        if (crashUrl != null) {
            this.mCrashUrl = crashUrl;
        }
        if (name != null) {
            this.name = name;
        }
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置我们的CrashHandler为系统默认的UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(this);
        //android 6.0以上动态申请权限
        //返回所需权限
        callBack.needPermissions(all_need_permissions);
    }
    /**
     * 回调接口
     */
    public interface CallBack {
        /**
         * android 6.0以上动态申请权限
         * 拒绝权限无法正常工作
         *
         * @param permissions
         */
        void needPermissions(String[] permissions);

    }

    /**
     * 当出现未捕获的异常时，
     * 系统自动调用这个回调函数
     *
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "uncaughtException: "+e );
        if (!handleException(e) && mDefaultHandler != null) {
            //如果自己没处理，则交给系统处理
            mDefaultHandler.uncaughtException(t, e);
        } else {
            //如果实在没办法,友好弹窗并退出程序杀死进程
            //有好弹窗
            showFriendExit();
            //睡眠2s
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            //退出程序
            AppManager.getAppManager().AppExit(application);
        }
    }

    /**
     * 自己处理未捕获的异常
     * 处理包括：
     * 1.收集未捕获的错误信息和具体的运行环境信息（手机信息）
     * 保存并发送到服务器
     * 2.友好弹窗关闭应用
     *
     * @param e
     * @return
     */
    private boolean handleException(Throwable e) {
        Log.e(TAG, "handleException: ");
        if (e == null) {
            return false;
        }
        //手机设备参数信息
        collectDeviceInfo();
        //添加自定义信息
        addCustomeInfo();
        //在非UI线程中显示Toast或dialog
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                showFriendExit();
                Looper.loop();
            }
        }).start();
        //保存日志文件
        Log.e(TAG, "handleException: 1111" );
        String fileName = saveCrashInfo2File(e);
//        Log.e(TAG, "handleException: " );
        //发送到服务器
        if (fileName != null && mCrashUrl != null) {
            HashMap<String,File> fileHashMap=new HashMap<>();
            fileHashMap.put(fileName,new File(fileName));
            sendCrash2Server(name, fileHashMap);
        }
        return true;
    }

    private void sendCrash2Server(String name, HashMap<String, File> fileHashMap) {
        OkHttpHelper.getInstance().upLoadFile(mCrashUrl,name,fileHashMap,null);

    }

    /**
     * 保存日志文件
     *
     * @param e
     * @return 文件全路径名称
     */
    private String saveCrashInfo2File(Throwable e) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : paramMap.entrySet()
                ) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        //将错误信息写入输出流
        e.printStackTrace(printWriter);
        //追踪异常栈
        Throwable cause = e.getCause();
        while (cause != null) {
            //将追踪到的异常写入输出流
            cause.printStackTrace(printWriter);
            //继续追踪异常栈
            cause = cause.getCause();
        }
        printWriter.close();
        //将输出流中的内容（即追踪的异常栈信息）转为String
        String result = writer.toString();
        stringBuffer.append(result);
        //获取时间戳
        long timeStamp = System.currentTimeMillis();
        //格式化时间
        String time = simpleDateFormat.format(new Date(timeStamp));
        //文件名字
        String fileName = mApplicationContext.getPackageName() + "-crash-" + time + "-" + timeStamp + ".log";
        //判断存储卡是否安装
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //存储路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/";
            File dir = new File(path);
            if (!dir.exists()) {//如果路径不存在
                dir.mkdirs();//创建路径系列
            }
            try {
                FileOutputStream fs = new FileOutputStream(path + fileName);
                fs.write(stringBuffer.toString().getBytes());
                fs.flush();
                fs.close();
                return path + fileName;
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                Log.e(TAG, "saveCrashInfo2File: "+e);
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(TAG, "saveCrashInfo2File: "+e1 );

            }


        }
        return null;

    }


    /**
     * 添加自定义信息
     */
    private void addCustomeInfo() {
        Log.e(TAG, "addCustomeInfo: " );
    }

    /**
     * 收集设备信息
     */
    private void collectDeviceInfo() {
        //获取VersonCode,VersionName
        PackageManager packageManager = mApplicationContext.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(mApplicationContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                String versionName = String.valueOf(packageInfo.versionName+"");
                Log.e(TAG,versionName);
                String versionCode = packageInfo.versionCode + "";
                paramMap.put("versionCode", versionCode);
                paramMap.put("versionName", versionName);

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "collectDeviceInfo: "+e );
        }
        //用反射获取所有系统信息
        Field[] fields = Build.class.getFields();
        for (Field field : fields
                ) {
            field.setAccessible(true);//设置为可获取
            try {
                //获取静态域的值 field.get(null)即可，具体看源码说明
                paramMap.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 关闭应用前友好弹窗
     */
    private void showFriendExit() {

        Toast.makeText(mApplicationContext, "程序开小差了呢...", Toast.LENGTH_SHORT).show();
    }


}
