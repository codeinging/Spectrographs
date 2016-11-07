package com.icephone.mphone.spectrograph.ui.actiivty;
/*
 * UVCCamera
 * library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: MainActivity.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
 * Files in the jni/libjpeg, jni/libusb, jin/libuvc, jni/rapidjson folder may have a different license, see the respective files.
*/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.icephone.mphone.spectrograph.R;
import com.icephone.mphone.spectrograph.model.RGB;
import com.icephone.mphone.spectrograph.ui.widget.ChartLogic.LuminIntensity;
import com.icephone.mphone.spectrograph.ui.widget.ChartLogic.SupportLineUtils;
import com.icephone.mphone.spectrograph.ui.widget.UVCCameraTextureView;
import com.icephone.mphone.spectrograph.utils.ThreadPoolUtils;
import com.orhanobut.logger.Logger;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.IButtonCallback;
import com.serenegiant.usb.IStatusCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.icephone.mphone.spectrograph.model.Constant.flag_pause;

public final class ScanActivity extends Activity implements CameraDialog.CameraDialogParent, UVCCameraTextureView.onUpdateListener {

    // for thread pool
    private static final int CORE_POOL_SIZE = 1;        // initial/minimum threads
    private static final int MAX_POOL_SIZE = 4;            // maximum threads
    private static final int KEEP_ALIVE_TIME = 10;        // time periods while keep the idle thread
    protected static final ThreadPoolExecutor EXECUTER
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private static Handler handler;
    private static int height = 480;
    private static int width = 640;
    private USBMonitor mUSBMonitor;
    private UVCCamera mUVCCamera;
    private UVCCameraTextureView mUVCCameraView;
    private ToggleButton open_close;
    private  ToggleButton plus_sub;
    private ToggleButton pause_continue;
    private Button saveButton;
    private Surface mPreviewSurface;
    LinearLayout chartParent;
    private static XYSeries series;
    private static XYMultipleSeriesDataset dataset;
    private String titles = "波长 WaveLength";            //设置表格下方显示的文字
    private static GraphicalView chart;
    private static Bitmap mBitmap = null;
    private SupportLineUtils mLineUtils;
    private static boolean subBgFlag = false;
    private static double[] addBgResult = new double[640];    //用来保存点击测-背时的测+背的值
    private  double[] result = new double[640];    //用来保存点击测-背时的测+背的值
    private static String TAG = MainActivity.class.getSimpleName();
    private static FileWriter writer;
    private static String SDPATH;
    private XYMultipleSeriesRenderer render;
    private Context mContext;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_layout);
        mContext=this;
        initView();
        initchart();
        mUVCCameraView = (UVCCameraTextureView) findViewById(R.id.UVCCameraTextureView1);
        mUVCCameraView.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float) UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mUVCCameraView.setonUpdateListener(this);
        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);

        SDPATH = Environment.getExternalStorageDirectory().getPath();
//        测试后发现，使用android自己的API设置的优先级，相比java，对线程调度影响显著。
//        myMathHandlerThread.setOsPriority(Process.THREAD_PRIORITY_FOREGROUND);
        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x122) {
//

                }
            }
        };*/
//        register();
    }

    private void register() {
        mUSBMonitor.register();
        if (mUVCCamera != null)
            mUVCCamera.startPreview();
    }

    private void initView() {
        open_close = (ToggleButton) findViewById(R.id.capture_button);
        plus_sub = (ToggleButton) findViewById(R.id.addBg_button);
        pause_continue = (ToggleButton) findViewById(R.id.pause_button);
        saveButton = (Button) findViewById(R.id.save_button);
        open_close.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    start_Preview();
                }else {
                   stopPreview();

                }
            }
        });
        plus_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){//测-背
                    sub_BackGround();
                }else {//测+背
                    add_BackGround();
                }

            }
        });
        pause_continue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//暂停
                    if (!flag_pause) {
                        flag_pause = true;
                        saveButton.setEnabled(true);
                        Toast.makeText(mContext,"暂停您可以点击曲线的某一位置查看具体数值哦！",Toast.LENGTH_SHORT).show();
                        plus_sub.setEnabled(false);
                    }
                }else {
                    if (flag_pause) {
                        flag_pause = false;
                        saveButton.setEnabled(false);
                        plus_sub.setEnabled(true);
                    }
                }

            }
        });
        saveButton.setOnClickListener(mOnClickListener);
//        pause_continue.setEnabled(false);
//        pause_continue.setChecked(false);
//        plus_sub.setEnabled(false);
//        plus_sub.setChecked(false);
//
//        saveButton.setEnabled(false);
    }

    /**
     * 扣除背景测量
     * * creat by syd
     */
    private void sub_BackGround() {
        mLineUtils.getXYMultipleSeriesRenderer().setYAxisMax(60000);
        mLineUtils.getXYMultipleSeriesRenderer().setYAxisMin(-10000);
        subBgFlag = true;
        addBgResult=result.clone();
    }

    /**
     * 带背景测量
     * creat by syd
     */
    private void add_BackGround() {
        subBgFlag = false;
        mLineUtils.getXYMultipleSeriesRenderer().setYAxisMax(100000);
        mLineUtils.getXYMultipleSeriesRenderer().setYAxisMin(5000);
    }

    /**
     * 开始预览
     * creat by syd
     */
    private void start_Preview() {
        if (mUVCCamera == null) {
            CameraDialog.showDialog(ScanActivity.this);
        }
        plus_sub.setChecked(false);
        plus_sub.setEnabled(true);

        pause_continue.setEnabled(true);
        pause_continue.setChecked(false);
    }

    /**
     * 停止预览
     * creat by syd
     */
    private void stopPreview() {
        if (mUVCCamera!=null){
            mUVCCamera.destroy();
            mUVCCamera = null;
        }
        Log.e(TAG, "stopPreview: " );

        pause_continue.setEnabled(false);
        pause_continue.setChecked(false);
        plus_sub.setEnabled(false);
        plus_sub.setChecked(false);

        saveButton.setEnabled(false);
    }

    /**
     * 初始化曲线图表
     * creat by syd
     */
    private void initchart() {
        chartParent = (LinearLayout) findViewById(R.id.chart);
        series = new XYSeries(titles);
        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        mLineUtils = new SupportLineUtils(this.getApplicationContext());
        mLineUtils.setmXYMultipleSeriesDataSet(dataset);
        render =mLineUtils.getXYMultipleSeriesRenderer();
        render.setBackgroundColor(Color.BLACK);
        chart = (GraphicalView) mLineUtils.initLineGraphView();
        chartParent.addView(chart);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUSBMonitor.register();
        if (mUVCCamera != null)
            mUVCCamera.startPreview();

    }

    @Override
    public void onPause() {
        release_camera();
        super.onPause();
    }


    private void release_camera() {
        if (mUVCCamera != null)
            mUVCCamera.stopPreview();
        mUSBMonitor.unregister();
    }
    @Override
    public void onDestroy() {
        if (mUVCCamera != null) {
            mUVCCamera.destroy();
            mUVCCamera = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mUVCCameraView = null;
        open_close = null;
        super.onDestroy();
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override

        public void onClick(final View view) {
            switch (view.getId()) {

//                        render.setZoomEnabled(true,true);
//                        chart.invalidate();
//                        mLineUtils.getXYMultipleSeriesRenderer().setZoomEnabled(true, true);
//						mLineUtils.getXYMultipleSeriesRenderer().setYAxisMax(YMax+500);
//						mLineUtils.getXYMultipleSeriesRenderer().setYAxisMin(YMin-200);
//						mLineUtils.getXYMultipleSeriesRenderer().setXAxisMax(XMax+500);
//						mLineUtils.getXYMultipleSeriesRenderer().setXAxisMin(XMin - 200);
                        // TODO: 2016/10/26 这里是什么意思
//

                case R.id.save_button:
                    Toast.makeText(ScanActivity.this,"已保存",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Toast.makeText(ScanActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if (mUVCCamera != null)
                mUVCCamera.destroy();
            mUVCCamera = new UVCCamera();
            EXECUTER.execute(new Runnable() {
                @Override
                public void run() {
                    mUVCCamera.open(ctrlBlock);
                    mUVCCamera.setStatusCallback(new IStatusCallback() {
                        @Override
                        public void onStatus(final int statusClass, final int event, final int selector,
                                             final int statusAttribute, final ByteBuffer data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ScanActivity.this, "onStatus(statusClass=" + statusClass
                                            + "; " +
                                            "event=" + event + "; " +
                                            "selector=" + selector + "; " +
                                            "statusAttribute=" + statusAttribute + "; " +
                                            "data=...)", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    mUVCCamera.setButtonCallback(new IButtonCallback() {
                        @Override
                        public void onButton(final int button, final int state) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ScanActivity.this, "onButton(button=" + button + "; " +
                                            "state=" + state + ")", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
//					mUVCCamera.setPreviewTexture(mUVCCameraView.getSurfaceTexture());
                    if (mPreviewSurface != null) {
                        mPreviewSurface.release();
                        mPreviewSurface = null;
                    }
                    try {
                        mUVCCamera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
                    } catch (final IllegalArgumentException e) {
                        // fallback to YUV mode
                        try {
                            mUVCCamera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                        } catch (final IllegalArgumentException e1) {
                            mUVCCamera.destroy();
                            mUVCCamera = null;
                        }
                    }
                    if (mUVCCamera != null) {
                        final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                        if (st != null)
                            mPreviewSurface = new Surface(st);
                        mUVCCamera.setPreviewDisplay(mPreviewSurface);
//						mUVCCamera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_RGB565/*UVCCamera.PIXEL_FORMAT_NV21*/);
                        mUVCCamera.startPreview();
                    }
                }
            });
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            // XXX you should check whether the comming device equal to camera device that currently using
            if (mUVCCamera != null) {
                mUVCCamera.close();
                if (mPreviewSurface != null) {
                    mPreviewSurface.release();
                    mPreviewSurface = null;
                }
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Toast.makeText(ScanActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
        }
    };

    /**
     * to access from CameraDialog
     *
     * @return
     */
    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    /**
     * 帧刷新
     *
     * @param bitmap
     */
    @Override
    public void onUpdate(Bitmap bitmap) {
        Log.e(TAG, "onUpdate: " );
        mBitmap = bitmap;
        ThreadPoolUtils.getFixedExecutorService().execute(handleBitmapRunnable);

    }

    Runnable handleBitmapRunnable = new Runnable() {
        @Override
        public void run() {
            if (!flag_pause) {
            //提高优先级
            Bitmap bitmap = mBitmap;
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
//            Log.e(TAG, "run: ");
            handleBitmapData(bitmap);

            }
        }
    };




    private void handleBitmapData(Bitmap bitmap) {
       /* Log.e(TAG, "handleBitmapData: " + String.valueOf(bitmap == null));
        if (bitmap != null) {
            try {
                Logger.d("处理数据");
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Logger.d(TAG, "width" + width);
                Logger.d(TAG, "height" + height);
                int[] pixel = new int[width * height];
                double[] pixel2 = new double[width * height];
                double[] yy = new double[width];
                double[] xx = new double[width];
                bitmap.getPixels(pixel, 0, width, 0, 0, width, height);

                Logger.d("处理数2");
                RGB[][] rgbs = new RGB[480][640];
                for(int i  = 0 ; i < 480; i++){
                    for(int j = 0; j < 640; j++){
                        rgbs[i][j] = new RGB(0,0,0);
                    }
                }
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int grey = pixel[i * width + j];
                        int R = ((grey & 0xFF0000) >> 16);
                        int G = ((grey & 0xFF00) >> 8);
                        int B = (grey & 0xFF);
                        rgbs[i][j].red = R;
                        rgbs[i][j].green = G;
                        rgbs[i][j].blue = B;
                        //
                        double grey1 = R * 0.3 + G * 0.59 + B * 0.11;

                        pixel2[i * width + j] = grey1;
                    }
                }
                Logger.d("处理数3");
                for (int a = 0; a < width; a++) {
                    xx[a] = 314.721 + 0.519 * a + 0.000039895 * a * a + 0.0000000096158 * a * a * a + 0d;
                }

                Logger.d("处理数4");
                for (int i = 0; i < width; i++) {
                    for (int j = 1; j < height; j++) {
                        pixel2[i] = pixel2[i] + pixel2[j * width + i];

                    }
                }


                for (int b = 0; b < width; b++) {
                    yy[b] = pixel2[b];
                }
//                series.clear();
                XYSeries series1=new XYSeries("123");

                double tempMax = 0;
                double tempMin = 200;
                *//*count++;
				File file  = new File(SDPATH + "//"+"TestResult.txt");
				if (writer == null){
					writer = new FileWriter(file);
				}
				writer.write("第" + count + "帧\n");
				writer.write("波长\t\t\t\t\t\t光强\n");
                for (int k = 0; k < width; k++) {
                    series1.add(xx[k], yy[k]);
					if(count < 100){
						writer.write(xx[k] + "\t\t\t\t" + yy[k] + "\n");
					}else if(count == 100){
						writer.close();
					}
//                    if (yy[k] > tempMax) {
//                        tempMax = yy[k];
//                    }
//                    if (yy[k] >= 200 && yy[k] < tempMin) {
//                        tempMin = yy[k];
//                    }
                }*//*


                LuminIntensity li = new LuminIntensity(640, 480);
//                Logger.d("rgb:"+rgbs[100][100].green);
//                Logger.d("rgb:"+rgbs[300][100].green);

                double[] result=li.getP(rgbs);
                XYSeries myseries=new XYSeries("hjhdhjhj");
                Random r=new Random();
//                Logger.e("x"+li.waveLength.length+"Y:"+);
                for (int k = 0; k < width; k++) {
                    myseries.add((int) li.waveLength[k],result[k]);
                }
//                YMax = (int) tempMax;
                synchronized (dataset){
                    dataset.clear();
                    dataset.addSeries(myseries);
                    chart.repaint();
                }
//					message.obj = series;
                Logger.d("发送消息");
//					handler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
                Logger.d("错误！！！！！！！！！" + e);
            }
        }*/
//=====================================================================
        if (bitmap != null) {
            try {
                Logger.d("处理数据");
                int[] pixel;
                pixel = new int[width * height];
                bitmap.getPixels(pixel, 0, width, 0, 0, width, height);
                RGB[][] rgbs = new RGB[480][640];
                for (int i = 0; i < 480; i++) {
                    for (int j = 0; j < 640; j++) {
                        rgbs[i][j] = new RGB(0, 0, 0);
                    }
                }
                int r = 0;
                int g = 0;
                int b = 0;
                int grey;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        grey = pixel[i * width + j];
                        r = ((grey & 0xFF0000) >> 16);
                        g = ((grey & 0xFF00) >> 8);
                        b = (grey & 0xFF);
//						double grey1 = R * 0.3 + G * 0.59 + B * 0.11;
//						pixel2[i * width + j] = grey1;

                        rgbs[i][j].red = r;
                        rgbs[i][j].green = g;
                        rgbs[i][j].blue = b;

                    }
                }
                LuminIntensity li = new LuminIntensity(640, 480);
                XYSeries myseries = new XYSeries("波长");
                double[] myresult;        //存储图像的纵坐标，横坐标在li.waveLength


                myresult = li.getP(rgbs);
                result=myresult;

                if (!subBgFlag) {
                    for (int k = 0; k < width; k++) {
                        myseries.add((int) li.waveLength[k], myresult[k]);
                    }

                } else {
                    for (int k = 0; k < width; k++) {
                        myseries.add((int) li.waveLength[k], myresult[k] - addBgResult[k]);
                    }

                }
                synchronized (dataset) {
                    if (!flag_pause) {
                        dataset.clear();
                        dataset.addSeries(myseries);
                        Logger.e("repaint");
                        chart.repaint();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.d("错误！！！！！！！！！" + e);
            }
        }
    }
}
