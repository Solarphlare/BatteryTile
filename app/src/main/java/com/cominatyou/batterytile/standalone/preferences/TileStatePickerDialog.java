package com.cominatyou.batterytile.standalone.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.batterytile.standalone.R;
import com.cominatyou.batterytile.standalone.databinding.TileStateDialogHeaderLayoutBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class TileStatePickerDialog {
    public static void show(Context context, LayoutInflater inflater, Runnable completionHandler) {
        int[] choice = { 0 };
        final SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        final TileStateDialogHeaderLayoutBinding binding = TileStateDialogHeaderLayoutBinding.inflate(inflater);

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.SingleSelectDialogTheme)
                .setSingleChoiceItems(new String[] {context.getString(R.string.dialog_tile_state_picker_always_on), context.getString(R.string.dialog_tile_state_picker_on_when_charging), context.getString(R.string.dialog_tile_state_picker_always_off) }, preferences.getInt("tileState", 0), (dialog, which) -> choice[0] = which)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    preferences.edit().putInt("tileState", choice[0]).apply();
                    completionHandler.run();
                    dialog.dismiss();
                })
                .setCustomTitle(binding.getRoot());

        final AlertDialog dialog = builder.show();
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button2))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));

    }
}
