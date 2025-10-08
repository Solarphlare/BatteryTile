package com.cominatyou.batterytile.preferences;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.TextView;

import com.cominatyou.batterytile.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TileTextDisambiguationDialog {
    public static void show(Context context) {
        int[] choice = { 0 };

        TextView title = new TextView(context);
        title.setText(R.string.dialog_tile_text_disambiguation_title);
        title.setGravity(Gravity.CENTER);
        title.setPadding(60, 60, 60, 10);
        title.setTextSize(24);

        new MaterialAlertDialogBuilder(context)
                .setSingleChoiceItems(new String[] { context.getString(R.string.bottom_sheet_tile_text_disambiguation_charging_text_title), context.getString(R.string.bottom_sheet_tile_text_disambiguation_discharging_text_title) }, 0, (dialog, which) -> choice[0] = which)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    context.startActivity(new Intent(context, EditTileTextActivity.class).putExtra("isEditingChargingText", choice[0] == 0));
                    dialog.dismiss();
                })
                .setCustomTitle(title)
                .show();

    }
}
