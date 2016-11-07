package com.icephone.mphone.spectrograph.utils.okhttphelpr;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.icephone.mphone.spectrograph.utils.okhttphelpr.callback.BaseCallBack;
import com.icephone.mphone.spectrograph.utils.okhttphelpr.callback.ProgressCallBack;
import com.icephone.mphone.spectrograph.utils.okhttphelpr.requestbody.ProgressRequestBody;
import com.icephone.mphone.spectrograph.utils.okhttphelpr.responcebody.ProgressResponceBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Okhttp封装
 * 这里使用gson解析，也可以更换其他的
 * Created by syd on 2016/9/10.
 */
public class OkHttpHelper {
    private static OkHttpClient okHttpClient;
    private static Gson gson;
    private static  OkHttpHelper mInstance;
    private String TAG="OkHttpHelper";
    private static Handler handler;
    private int JSON_PARSE_EXCEPTION_CODE=000;
    private int IO_EXCEPTION_CODE=001;
    private String restr;
    private Object resObject;
    private Request.Builder builder;
    private RequestBody requestBody;
    private FormBody.Builder formBodyBuilder;
    private Request request;
    private Call call;
    private MultipartBody multipartBody;

    //将构造函数私有化，单利模式要素之一
    private OkHttpHelper() {
    }
    public static OkHttpHelper getInstance(){
        if (mInstance==null){
            mInstance =new OkHttpHelper();
        }
        if (okHttpClient==null) {
            okHttpClient = new OkHttpClient();
        }
        if (gson==null){
            gson=new Gson();
        }
        if (handler==null){
            handler=new Handler(Looper.getMainLooper());
        }
        return mInstance;
    }
    /**
     * 异步Get
     * @param url
     * @param baseCallBack
     * @return Call用于取消请求
     * */
    public Call get(String url,BaseCallBack baseCallBack){
        request=buildRequest(url,null, HttpMethodType.GET);
        return doRequest(request,baseCallBack);
    }
    /**
     * 异步post
     * @param url
     * @param params
     * @param baseCallBack
     * @return Call用于取消请求
     * */
    public Call post(String url, Map<String,String>params,BaseCallBack baseCallBack){
        requestBody=buildRequestBody(params);
        request=buildRequest(url,requestBody, HttpMethodType.POST);
        Log.e(TAG, String.valueOf("post: "+request==null));
        return doRequest(request,baseCallBack);
    }
    /**
     * 异步上传文件附带参数
     * @param url
     * @param name 表单文件项名
     * @param params 其他表单参数
     * @param files 文件：map-filename->file
     * @param baseCallBack
     * @return Call用于取消请求
     * */

    public Call upLoadFileWithParams(String url,String name,Map<String,String> params,Map<String,File> files,ProgressCallBack baseCallBack){
        multipartBody=buildMultiRequestBody(name,params,files);
        request=buildProgressRequest(url,new ProgressRequestBody(multipartBody,baseCallBack), HttpMethodType.POST);
        call=doRequest(request,baseCallBack);
        return call;
    }

    /**
     * 上传文件
     * @param url
     * @param name 表单文件项名
     * @param files 文件：map-filename->file
     * @param baseCallBack
     * @return
     */
    public Call upLoadFile(String url,String name,Map<String,File> files,ProgressCallBack baseCallBack){
        multipartBody=buildMultiRequestBody(name,null,files);
        request=buildProgressRequest(url,new ProgressRequestBody(multipartBody,baseCallBack), HttpMethodType.POST);
        call=doRequest(request,baseCallBack);
        return call;
    }
    public Call downLoadFile(String url,Map<String,String> params,String savaPath,ProgressCallBack progressCallBack){
        requestBody=buildRequestBody(params);
        request=buildRequest(url,requestBody, HttpMethodType.POST);
        call=doDownLoand(request,savaPath,progressCallBack);
        return call;
    }

    private Call doDownLoand(final Request request, final String savaPath, final ProgressCallBack progressCallBack) {
        progressCallBack.onRequestBefore(request);
        // 为客户端实例添加网络拦截器，并相应回调。
        addIntercepter(request, progressCallBack);
        call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (progressCallBack!=null) {
                    progressCallBack.onFailure(call, e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressCallBack!=null) {
                    if (response.isSuccessful()) {
                        InputStream inputStream = response.body().byteStream();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(savaPath));
                            byte[] buffer = new byte[2048];
                            int len = 0;
                            while ((len = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, len);
                            }
                            fileOutputStream.flush();
                            try {
                                restr = response.body().string();
                            } catch (IOException e) {
                                Log.e(TAG, "onResponse: 此下载请求除文件之外没有返回数据");
                                restr = null;
                            }
                            if (restr != null) {
                                Log.i(TAG, "onResponse: " + restr);
                                if (progressCallBack.type == String.class) {
                                    progressCallBack.onSuccess(response, restr);
                                } else {
                                    Object o = gson.fromJson(restr, progressCallBack.type);
                                    progressCallBack.onSuccess(response, o);
                                }
                            } else {
                                progressCallBack.onSuccess(response, null);
                            }
                            Log.i(TAG, "onResponse: 下载成功");
                        } catch (IOException e) {
                            Log.e(TAG, "onResponse: " + e);
                            progressCallBack.onError(response, IO_EXCEPTION_CODE, e);
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            Log.e(TAG, "onResponse: 数据解析失败");
                            progressCallBack.onError(response, JSON_PARSE_EXCEPTION_CODE, e);
                        }
                    } else {
                        progressCallBack.onError(response, response.code(), null);
                    }
                }
            }
        });
        return call;
    }
    /**
     * 内部进行请求操作函数
     * @param request
     * @param baseCallBack
     * @return  Call用于取消请求
     * */
    private Call doRequest(final Request request, final BaseCallBack baseCallBack){
        if (baseCallBack!=null) {
            baseCallBack.onRequestBefore(request);
        }
        call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (baseCallBack!=null) {
                    callBackFailure(baseCallBack, call, e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (baseCallBack!=null) {
                    if (response.isSuccessful()) {
                        restr = response.body().string();
                        Log.i(TAG, ": 服务器返回字符串：" + restr);
                        if (baseCallBack.type == String.class) {
                            callBackSuccess(baseCallBack, response, restr);
                        } else {
                            //                            Object o=JSON.parseObject(restr,baseCallBack.type);
                            try {
                                resObject = gson.fromJson(restr, baseCallBack.type);
                                callBackSuccess(baseCallBack, response, resObject);
                            } catch (JsonParseException e) {
                                e.printStackTrace();
                                callBackError(baseCallBack, response, JSON_PARSE_EXCEPTION_CODE, null);
                            }
                        }
                    } else {
                        callBackError(baseCallBack, response, response.code(), null);
                    }
                }
            }
        });
        return call;
    }
    /**
     * 构建ProgressRequest request
     * @param url
     * @param progressRequestBody
     * @param methodType
     * */
    private Request buildProgressRequest(String url,ProgressRequestBody progressRequestBody, HttpMethodType methodType){
        builder=new Request.Builder();
        builder.url(url);
        if (methodType== HttpMethodType.GET){
            builder.get();
        }else if (methodType== HttpMethodType.POST){
            builder.post(progressRequestBody);
        }
        return builder.build();
    }
    /**
     * 构建request
     * @param url
     * @param requestBody
     * @param methodType
     * */
    private Request buildRequest(String url,RequestBody requestBody, HttpMethodType methodType){
        builder=new Request.Builder();
        builder.url(url);
        if (methodType== HttpMethodType.GET){
            builder.get();
        }else if (methodType== HttpMethodType.POST){
            builder.post(requestBody);
        }
        return builder.build();
    }
    /**
     * 构建普通参数 requestbody
     * @param map
     * @return RequestBody
     * */
    private RequestBody buildRequestBody(Map<String,String> map){
         formBodyBuilder=new FormBody.Builder();
        if (map!=null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return formBodyBuilder.build();
    }
    /**
     * 构建参数与文件组合 MultiRequestBody
     * @param name 文件表单参数名
     * @param params 表单参数
     * @param fileMap 文件名和文件map
     * @return MultipartBody
     * */
    private MultipartBody buildMultiRequestBody(String name,Map<String, String> params, Map<String,File> fileMap){
        /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder multipartBodyBuilder=new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);
        //添加参数,for each需要判空
        if (params!=null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody;
        //添加多文件,不确定类型，二进制流
        MediaType mediaType=MediaType.parse("application/octet-stream");
        for (Map.Entry<String,File> file:fileMap.entrySet()
                ) {
            requestBody=RequestBody.create(mediaType,file.getValue());
            multipartBodyBuilder.addFormDataPart(name,file.getKey(),requestBody);
        }
        MultipartBody multipartBody=multipartBodyBuilder.build();
        return multipartBody;
    }
    /**
     * 为ohhttpclient添加拦截器，实现进度回调
     * @param request
     * @param progressCallBack
     * */
    private void addIntercepter(final Request request, final ProgressCallBack progressCallBack) {
        okHttpClient=new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response=chain.proceed(request);
                return response.newBuilder().body(new ProgressResponceBody(response.body(),progressCallBack))
                        .build();
            }
        }).build();
    }
    /**
     * 回调主线程发起请求失败
     * @param baseCallBack
     * @param call
     * @param e
     * */
    private void callBackFailure(final BaseCallBack baseCallBack, final Call call, final IOException e){
        handler.post(new Runnable() {
            @Override
            public void run() {
                baseCallBack.onFailure(call,e);
            }
        });
    }
    /**
     * 回调主线程发起请求服务器返回失败
     * @param baseCallBack
     * @param response
     * @param code
     * */
    private void callBackError(final BaseCallBack baseCallBack, final Response response, final int code, final Exception e){
        handler.post(new Runnable() {
            @Override
            public void run() {
                baseCallBack.onError(response,code,e);
            }
        });
    }
    /**
     * 回调主线程发起请求成功并返回解析过的数据
     * @param baseCallBack
     * @param response
     * @param obj
     * */
    private void callBackSuccess(final BaseCallBack baseCallBack, final Response response, final Object obj){
        handler.post(new Runnable() {
            @Override
            public void run() {
                baseCallBack.onSuccess(response,obj);
            }
        });
    }
    /**
     * 请求方式
     * */
    enum HttpMethodType{
        GET,
        POST
    }

}
