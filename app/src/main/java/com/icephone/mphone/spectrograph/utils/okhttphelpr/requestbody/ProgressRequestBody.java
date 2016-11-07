package com.icephone.mphone.spectrograph.utils.okhttphelpr.requestbody;

import android.os.Handler;

import com.icephone.mphone.spectrograph.utils.okhttphelpr.callback.ProgressCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by syd on 2016/9/11.
 */
public class ProgressRequestBody extends RequestBody {
    public RequestBody requestBody;
    private ProgressCallBack progressCallBack=null;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;
    private Handler mHandler;
    private int percent;
    private boolean done;
    private String TAG = "ProgressRequestBody";


    public ProgressRequestBody(RequestBody requestBody, ProgressCallBack progressCallBack) {
        this.requestBody = requestBody;
        if (progressCallBack!=null) {
            this.progressCallBack = progressCallBack;
        }

    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                percent = (int) ((100 * bytesWritten) / contentLength());
                if (percent >= 100) {
                    done = true;
                }
                if (progressCallBack!=null) {
                    updataProgress(bytesWritten, contentLength, percent, progressCallBack);
                }

            }
        };
    }

    /**
     * 回调 UI进度
     *
     * @param totalBytesWrite
     * @param lenth
     * @param percent
     * @param progressCallBack
     */
    private void updataProgress(final long totalBytesWrite, final long lenth, final int percent, final ProgressCallBack progressCallBack) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressCallBack.onProgress(totalBytesWrite, lenth, percent, done);
                }
            });
        }

    }
}
