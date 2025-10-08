package com.cominatyou.batterytile.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


import com.cominatyou.batterytile.R;
import com.cominatyou.batterytile.databinding.BottomSheetPreferencesBinding;
import com.cominatyou.batterytile.debug.DebugDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PreferencesBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetPreferencesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetPreferencesBinding.inflate(inflater, container, false);
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        binding.infoInTitlePreferenceLayout.setOnClickListener(self -> binding.infoInTitleSwitch.toggle());
        binding.infoInTitleSwitch.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("infoInTitle", state).apply());

        binding.dynamicTileIconLayout.setOnClickListener(self -> binding.dynamicTileIconSwitch.toggle());
        binding.dynamicTileIconSwitch.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("dynamic_tile_icon", state).apply());

        binding.dynamicTileIconSwitch.setChecked(preferences.getBoolean("dynamic_tile_icon", true));

        binding.infoInTitleSwitch.setChecked(preferences.getBoolean("infoInTitle", false));

        binding.tileStateLayout.setOnClickListener(v -> TileStatePickerDialog.show(requireContext(), this::updateTileStateDescription));
        updateTileStateDescription();

        binding.tileTextLayout.setOnClickListener(v -> TileTextDisambiguationDialog.show(requireContext()));

        binding.preferencesBottomSheetTitle.setOnLongClickListener(l -> {
            DebugDialog.show(requireContext());
            return true;
        });

        return binding.getRoot();
    }

    private void updateTileStateDescription() {
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        switch (preferences.getInt("tileState", 0)) {
            case 0 -> binding.tileStateDescription.setText(R.string.tile_state_always_on);
            case 1 -> binding.tileStateDescription.setText(R.string.tile_state_on_when_charging);
            case 2 -> binding.tileStateDescription.setText(R.string.tile_state_always_off);
        }
    }

    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        requireActivity().finish();
    }
}
