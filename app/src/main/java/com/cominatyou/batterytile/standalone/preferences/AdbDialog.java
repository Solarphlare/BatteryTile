package com.cominatyou.batterytile.standalone.preferences;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.batterytile.standalone.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class AdbDialog {
    public static void show(Context context) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.requires_adb_dialog_title)
                .setMessage(Html.fromHtml(context.getString(R.string.requires_adb_dialog_description) + "<br><br><tt>adb shell pm grant " + context.getPackageName() + " " + Manifest.permission.WRITE_SECURE_SETTINGS +
                        "</tt><br><br>" + context.getString(R.string.requires_adb_dialog_description_second_half), Html.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setNeutralButton(R.string.requires_adb_dialog_copy_button, (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", "adb shell pm grant " + context.getPackageName()+ " " + Manifest.permission.WRITE_SECURE_SETTINGS);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.requires_adb_dialog_copy_success, Toast.LENGTH_LONG).show();
                });

        final AlertDialog dialog = builder.show();
        ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex));
        @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier("alertTitle", "id", context.getPackageName());
        ((TextView) Objects.requireNonNull(dialog.findViewById(resId))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button3))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button2))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
    }
}