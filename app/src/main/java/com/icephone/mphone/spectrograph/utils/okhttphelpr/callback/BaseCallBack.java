package com.icephone.mphone.spectrograph.utils.okhttphelpr.callback;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by syd on 2016/9/10.
 */
public abstract class BaseCallBack<T> {
    private final String TAG="BaseCallBack";
    public Type type;
    public BaseCallBack() {
        type =getSuperclassTypeParameter(getClass());
        Log.e(TAG, "BaseCallBack: type:"+type);
    }
    /**
     * 获取泛型的类类型
     * @param clazz
     * */
    static Type getSuperclassTypeParameter(Class<?> clazz){
        //getSuperclass()是获取父类
        //getGenericSuperclass获取带有泛型的父类
        Type superclass=clazz.getGenericSuperclass();
//        if (!(superclass instanceof Class)){
//            throw new RuntimeException("Missing type parameter");
//        }
        //ParameterizedType参数化类型，即泛型
        ParameterizedType parameterizedType=(ParameterizedType)superclass;
//        return $GparameterizedType.getActualTypeArguments()[0];
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        return parameterizedType.getActualTypeArguments()[0];
    }
    /**
     * 发起网络请求前调用
     * @param request
     * */
    public abstract void onRequestBefore(Request request);
    /**
     * 发起网络请求失败
     * @param call
     * @param e
     * */
    public abstract void onFailure(Call call, IOException e);
    /**
     * 发起网络请求成功并返回根据泛型解析的数据
     * @param response
     * @param t
     * */
    public abstract void onSuccess(Response response, T t);
    /**
     * 服务器返回失败
     * @param response
     * @param code 状态码，包含自定义数据解析错误码，文件IOException
     * @param e
     * */
    public abstract void onError(Response response, int code, Exception e);

}
