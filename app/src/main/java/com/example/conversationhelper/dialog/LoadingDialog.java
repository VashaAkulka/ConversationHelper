package com.example.conversationhelper.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog {
    private final ProgressDialog progressDialog;

    public LoadingDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Ожидайте загрузки данных...");
        progressDialog.setCancelable(false);
    }

    public void show() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
