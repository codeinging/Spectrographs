package com.icephone.mphone.spectrograph.utils.okhttphelpr.callback;

/**
 * Created by syd on 2016/9/11.
 */
public abstract class ProgressCallBack<T> extends BaseCallBack<T> {
    public ProgressCallBack() {
    }

    /**
     *进度调用
     * @param byesReaded 已经下载或者上传的自己的字节数
     * @param contentLenth 下载或者上传的文件总长度
     * @param percent 完成百分比
     * @param done 是否完成
     * */
    public abstract void onProgress(long byesReaded,long contentLenth,int percent,boolean done);
}
