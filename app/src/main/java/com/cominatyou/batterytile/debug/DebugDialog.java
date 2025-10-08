package com.cominatyou.batterytile.debug;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.cominatyou.batterytile.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebugDialog {
    static ExecutorService executorService = null;

    public static void show(final Context context) {
        executorService = Executors.newFixedThreadPool(3);

        final MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.debug_dialog_title))
                .setMessage(context.getString(R.string.debug_dialog_initial_description))
                .setView(R.layout.debug_dialog_layout)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    executorService.shutdownNow();
                    dialog.dismiss();
                })
                .setOnDismissListener((dialogInterface) -> executorService.close());

        final AlertDialog dialog = dialogBuilder.show();
        executorService.submit(() -> new DebugInfoCollector().collectDebugInfo(context, dialog, executorService));
    }
}
