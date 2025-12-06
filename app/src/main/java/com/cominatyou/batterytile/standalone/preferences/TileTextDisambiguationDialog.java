package com.cominatyou.batterytile.standalone.preferences;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.cominatyou.batterytile.standalone.databinding.TileTextDialogLayoutBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TileTextDisambiguationDialog {
    public static void show(final Context context, final LayoutInflater layoutInflater) {
        final TileTextDialogLayoutBinding binding = TileTextDialogLayoutBinding.inflate(layoutInflater);
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot())
                .create();


        binding.tileTextDisambiguationDialogCancelButton.setOnClickListener(v -> dialog.dismiss());

        binding.tileTextDisambiguationDialogChargingTextButton.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditTileTextActivity.class).putExtra("isEditingChargingText", true));
            dialog.dismiss();
        });

        binding.tileTextDisambiguationDialogDischargingTextButton.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditTileTextActivity.class).putExtra("isEditingChargingText", false));
            dialog.dismiss();
        });

        dialog.show();
    }
}
