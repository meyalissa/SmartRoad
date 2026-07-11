package com.smartroad.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartroad.R;

/** Shared non-cancelable "Loading…" dialog used by every form-submission screen. */
public final class LoadingDialogHelper {

    private LoadingDialogHelper() { }

    public static AlertDialog show(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        dialog.show();
        return dialog;
    }

    public static void hide(AlertDialog dialog) {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }
}
