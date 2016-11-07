package com.icephone.mphone.spectrograph.utils.okhttphelpr.responcebody;

import android.os.Handler;
import android.os.Looper;

import com.icephone.mphone.spectrograph.utils.okhttphelpr.callback.ProgressCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by syd on 2016/9/11.
 */
public class ProgressResponceBody extends ResponseBody {
    private ResponseBody responseBody;
    private BufferedSource bufferedSource;
    private ProgressCallBack progressCallBack;
    private int percent;
    private boolean done=false;
    private Handler mHandler;

    public ProgressResponceBody(ResponseBody responseBody,ProgressCallBack progressCallBack) {
        this.responseBody = responseBody;
        this.progressCallBack=progressCallBack;
        mHandler=new Handler(Looper.getMainLooper());
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource==null){
            //包装
            bufferedSource= Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }
    private Source source(Source source) {

        return new ForwardingSource(source) {

            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = 0;
                try {
                    bytesRead = super.read(sink, byteCount);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
//                progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                // TODO: 2016/9/11 updata
                percent=(int)((100*totalBytesRead)/responseBody.contentLength());
                if (percent>=100){
                    done=true;
                }
                updataProgress(totalBytesRead,responseBody.contentLength(),percent,progressCallBack);
                return bytesRead;
            }
        };
    }
/**
 * 回调 UI进度
 * @param totalBytesRead
 * @param lenth
 * @param percent
 * @param progressCallBack
* */
    private void updataProgress(final long totalBytesRead, final long lenth, final int percent, final ProgressCallBack progressCallBack) {
        if (mHandler!=null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                     progressCallBack.onProgress(totalBytesRead,lenth,percent,done);
                }
            });
        }

    }
}

