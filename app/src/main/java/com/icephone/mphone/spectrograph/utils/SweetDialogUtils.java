package com.icephone.mphone.spectrograph.utils;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by syd on 2016/10/18.
 */

public class SweetDialogUtils {

    private SweetDialogUtils() {
    }
    public static void showWarningDialog(Context context, String contentText,
                            String confirmText, SweetAlertDialog.OnSweetClickListener confirmListener,
                            SweetAlertDialog.OnSweetClickListener cancelListener){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText(contentText)
                .setConfirmText(confirmText)
                .setTitleText("提醒")
                .setConfirmClickListener(confirmListener).setCancelClickListener(cancelListener).show();
    }


}
