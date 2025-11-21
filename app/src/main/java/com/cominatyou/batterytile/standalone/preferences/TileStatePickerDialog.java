package com.cominatyou.batterytile.standalone.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.batterytile.standalone.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class TileStatePickerDialog {
    public static void show(Context context, Runnable completionHandler) {
        int[] choice = { 0 };
        final SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        TextView title = new TextView(context);
        title.setText(R.string.dialog_tile_state_picker_title);
        title.setGravity(Gravity.CENTER);
        title.setPadding(60, 60, 60, 10);
        title.setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex));
        title.setTextSize(24);

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.SingleSelectDialogTheme)
                .setSingleChoiceItems(new String[] {context.getString(R.string.dialog_tile_state_picker_always_on), context.getString(R.string.dialog_tile_state_picker_on_when_charging), context.getString(R.string.dialog_tile_state_picker_always_off) }, preferences.getInt("tileState", 0), (dialog, which) -> choice[0] = which)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    preferences.edit().putInt("tileState", choice[0]).apply();
                    completionHandler.run();
                    dialog.dismiss();
                })
                .setCustomTitle(title);

        final AlertDialog dialog = builder.show();
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button2))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));

    }
}
