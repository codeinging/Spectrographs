package com.icephone.mphone.spectrograph.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icephone.mphone.spectrograph.ui.MyApplication;

/**
 * Created by 刘道兴 on 2016/10/29.
 */

public abstract class BaseFragment extends Fragment {
        private boolean isDebug;
        private String APP_NAME;
        protected final String TAG = this.getClass().getSimpleName();
        private View mContextView = null;
        protected Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            isDebug = MyApplication.isDebug;
            APP_NAME = MyApplication.APP_NAME;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContextView = inflater.inflate(bindLayout(), container, false);
            initView(mContextView);
            doBusiness();
            return mContextView;
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
         * [绑定布局]
         *
         * @return
         */
        public abstract int bindLayout();

        /**
         * [初始化控件]
         *
         * @param view
         */
        public abstract void initView(final View view);

        /**
         * [业务操作]
         *
         *
         */
        public abstract void doBusiness();


        @SuppressWarnings("unchecked")
        public <T extends View> T $(View view, int resId) {
            return (T) view.findViewById(resId);
        }

        /**
         * [日志输出]
         *
         * @param msg
         */
        protected void $Log(String msg) {
            if (isDebug) {
                Log.d(TAG, msg);
            }
        }

        /**
         * [防止快速点击]
         *
         * @return
         */
        private boolean fastClick() {
            long lastClick = 0;
            if (System.currentTimeMillis() - lastClick <= 1000) {
                return false;
            }
            lastClick = System.currentTimeMillis();
            return true;
        }
}

