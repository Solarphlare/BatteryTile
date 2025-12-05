package com.cominatyou.batterytile.standalone.preferences;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.batterytile.standalone.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class TileTextDisambiguationDialog {
    public static void show(final Context context) {
        int[] choice = { 0 };

        TextView title = new TextView(context);
        title.setText(R.string.dialog_tile_text_disambiguation_title);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex));
        title.setPadding(60, 60, 60, 10);
        title.setTextSize(24);

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.SingleSelectDialogTheme)
                .setSingleChoiceItems(new String[] { context.getString(R.string.bottom_sheet_tile_text_disambiguation_charging_text_title), context.getString(R.string.bottom_sheet_tile_text_disambiguation_discharging_text_title) }, 0, (dialog, which) -> choice[0] = which)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    context.startActivity(new Intent(context, EditTileTextActivity.class).putExtra("isEditingChargingText", choice[0] == 0));
                    dialog.dismiss();
                })
                .setCustomTitle(title);

        final AlertDialog dialog = builder.show();
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button2))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
        ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(context, R.font.gs_flex_medium));
    }
}
