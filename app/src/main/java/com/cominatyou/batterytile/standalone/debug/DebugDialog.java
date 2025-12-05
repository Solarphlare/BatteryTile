package com.cominatyou.batterytile.standalone.debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.cominatyou.batterytile.standalone.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebugDialog {
    static ExecutorService executorService = null;

    public static void show(final FragmentActivity activity) {
        executorService = Executors.newFixedThreadPool(3);

        final MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity)
                .setTitle(activity.getString(R.string.debug_dialog_title))
                .setMessage(activity.getString(R.string.debug_dialog_initial_description))
                .setView(R.layout.debug_dialog_layout)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    executorService.shutdownNow();
                    dialog.dismiss();
                })
                .setOnDismissListener((dialogInterface) -> executorService.close());

        final AlertDialog dialog = dialogBuilder.show();
        dialog.setOwnerActivity(activity);
        ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(ResourcesCompat.getFont(activity, R.font.gs_flex));
        @SuppressLint("DiscouragedApi") int resId = activity.getResources().getIdentifier("alertTitle", "id", activity.getPackageName());
        ((TextView) Objects.requireNonNull(dialog.findViewById(resId))).setTypeface(ResourcesCompat.getFont(activity, R.font.gs_flex));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button2))).setTypeface(ResourcesCompat.getFont(activity, R.font.gs_flex));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(activity, R.font.gs_flex));


        executorService.submit(() -> new DebugInfoCollector().collectDebugInfo(activity, dialog, executorService));
    }
}
