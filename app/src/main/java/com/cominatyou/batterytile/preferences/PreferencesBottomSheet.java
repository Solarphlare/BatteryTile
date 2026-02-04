package com.cominatyou.batterytile.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cominatyou.batterytile.R;
import com.cominatyou.batterytile.databinding.BottomSheetPreferencesBinding;
import com.cominatyou.batterytile.debug.DebugDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PreferencesBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetPreferencesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetPreferencesBinding.inflate(inflater, container, false);
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        binding.infoInTitlePreference.setOnClickListener(self -> binding.infoInTitlePreference.toggle());
        binding.infoInTitlePreference.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("infoInTitle", state).apply());

        binding.percentageAsTileIconPreference.setOnClickListener(self -> binding.percentageAsTileIconPreference.toggle());

        binding.percentageAsTileIconPreference.setOnCheckedChangeListener((self, state) -> {
            binding.dynamicTileIconPreference.setChecked(false);
            binding.dynamicTileIconPreference.setEnabled(!state);
            binding.dynamicTileIconPreference.setDescription(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_dynamic_tile_icon_description);

            preferences.edit().putBoolean("percentage_as_icon", state).apply();
        });


        binding.dynamicTileIconPreference.setOnClickListener(self -> binding.dynamicTileIconPreference.toggle());
        binding.dynamicTileIconPreference.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("dynamic_tile_icon", state).apply());

        if (preferences.getBoolean("percentage_as_icon", false)) {
            binding.percentageAsTileIconPreference.setChecked(true);
            binding.dynamicTileIconPreference.setChecked(false);
            binding.dynamicTileIconPreference.setDescription(R.string.bottom_sheet_preferences_tile_state_disabled_reason);
        }

        binding.dynamicTileIconPreference.setChecked(preferences.getBoolean("dynamic_tile_icon", true));

        binding.infoInTitlePreference.setChecked(preferences.getBoolean("infoInTitle", false));

        binding.tileStateLayout.setOnClickListener(v -> TileStatePickerDialog.show(requireContext(), inflater, this::updateTileStateDescription));
        updateTileStateDescription();

        binding.tileTextLayout.setOnClickListener(v -> TileTextDisambiguationDialog.show(requireContext(), inflater));

        binding.preferencesBottomSheetTitle.setOnLongClickListener(l -> {
            DebugDialog.show(requireActivity());
            return true;
        });

        return binding.getRoot();
    }

    private void updateTileStateDescription() {
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        switch (preferences.getInt("tileState", 0)) {
            case 0 -> binding.tileStateLayout.setDescription(R.string.tile_state_always_on);
            case 1 -> binding.tileStateLayout.setDescription(R.string.tile_state_on_when_charging);
            case 2 -> binding.tileStateLayout.setDescription(R.string.tile_state_always_off);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // only for tablets. expand the sheet all the way, otherwise only the title portion is shown on screen
        if ((requireContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            final BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            assert dialog != null;

            final View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;

            final BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        requireActivity().finish();
    }
}
