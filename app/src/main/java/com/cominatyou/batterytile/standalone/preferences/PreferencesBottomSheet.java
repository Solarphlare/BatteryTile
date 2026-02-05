package com.cominatyou.batterytile.standalone.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.widget.Button;
import android.widget.TextView;

import com.cominatyou.batterytile.standalone.R;
import com.cominatyou.batterytile.standalone.databinding.AdbDialogHeaderLayoutBinding;
import com.cominatyou.batterytile.standalone.databinding.BottomSheetPreferencesBinding;
import com.cominatyou.batterytile.standalone.databinding.PowerSaveWarningDialogHeaderLayoutBinding;
import com.cominatyou.batterytile.standalone.debug.DebugDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class PreferencesBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetPreferencesBinding binding;
    boolean showDialog = false;

    private void forceTappableTile(boolean force) {
        binding.tappableTilePreference.setChecked(force);
        binding.tappableTilePreference.setEnabled(!force);
    }

    private boolean checkIfPermissionIsDenied() {
        return requireContext().checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetPreferencesBinding.inflate(inflater, container, false);
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        binding.preferencesBottomSheetTitle.setOnLongClickListener(l -> {
            DebugDialog.show(requireActivity());
            return true;
        });

        binding.tappableTilePreference.setOnClickListener(self -> binding.tappableTilePreference.toggle());

        binding.tappableTilePreference.setOnCheckedChangeListener((self, state) -> {
            if (checkIfPermissionIsDenied() && state) {
                self.setChecked(false);
                AdbDialog.show(requireContext(), inflater);
            }
            else {
                if (state && showDialog) {
                    final PowerSaveWarningDialogHeaderLayoutBinding binding = PowerSaveWarningDialogHeaderLayoutBinding.inflate(inflater);

                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                            .setCustomTitle(binding.getRoot())
                            .setMessage(R.string.power_save_system_warning_description)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

                    final AlertDialog dialog = builder.show();

                    ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gs_text));
                    ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gs_text_medium));
                }
                preferences
                        .edit()
                        .putBoolean("tappableTileEnabled", state)
                        .apply();

                setTileStatePreferenceEnabled(!state);
            }
        });

        binding.emulatePowerSaveTilePreference.setOnClickListener(self -> binding.emulatePowerSaveTilePreference.toggle());

        binding.emulatePowerSaveTilePreference.setOnCheckedChangeListener((self, state) -> {
            if (checkIfPermissionIsDenied() && state) {
                self.setChecked(false);
                AdbDialog.show(requireContext(), inflater);
            }
            else {
                forceTappableTile(state);
                preferences.edit().putBoolean("emulatePowerSaveTile", state).apply();
                setTileStatePreferenceEnabled(!state);

                binding.tileTextLayout.setEnabled(!state);
                binding.tileTextLayout.setDescription(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_tile_text_description));

                binding.infoInTitlePreference.setChecked(false);
                binding.infoInTitlePreference.setEnabled(!state);
                binding.infoInTitlePreference.setDescription(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_percentage_as_tile_icon_description));

                binding.percentageAsTileIconPreference.setChecked(false);
                binding.percentageAsTileIconPreference.setEnabled(!state);
                binding.percentageAsTileIconPreference.setDescription(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_percentage_as_tile_icon_description));

                binding.dynamicTileIconPreference.setChecked(false);
                binding.dynamicTileIconPreference.setEnabled(!state);
                binding.dynamicTileIconPreference.setDescription(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_dynamic_tile_icon_description));

                binding.dynamicTileIconPreference.setChecked(false);
                binding.dynamicTileIconPreference.setEnabled(!state);
                binding.dynamicTileIconPreference.setDescription(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_dynamic_tile_icon_description));
            }
        });

        binding.infoInTitlePreference.setOnClickListener(self -> binding.infoInTitlePreference.toggle());
        binding.infoInTitlePreference.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("infoInTitle", state).apply());

        binding.percentageAsTileIconPreference.setOnClickListener(self -> binding.percentageAsTileIconPreference.toggle());

        binding.percentageAsTileIconPreference.setOnCheckedChangeListener((self, state) -> {
            if (binding.percentageAsTileIconPreference.isPressed()) {
                binding.dynamicTileIconPreference.setChecked(false);
            }
            preferences.edit().putBoolean("percentage_as_icon", state).apply();
        });

        binding.dynamicTileIconPreference.setOnClickListener(self -> binding.dynamicTileIconPreference.toggle());
        binding.dynamicTileIconPreference.setOnCheckedChangeListener((self, state) -> {
            if (binding.dynamicTileIconPreference.isPressed()) {
                binding.percentageAsTileIconPreference.setChecked(false);
            }
            preferences.edit().putBoolean("dynamic_tile_icon", state).apply();
        });

        if (preferences.getBoolean("emulatePowerSaveTile", false)) {
            binding.emulatePowerSaveTilePreference.setChecked(true);
            forceTappableTile(true);
        }
        else {
            binding.tappableTilePreference.setChecked(preferences.getBoolean("tappableTileEnabled", false));
        }

        if (preferences.getBoolean("percentage_as_icon", false)) {
            binding.percentageAsTileIconPreference.setChecked(true);
            binding.dynamicTileIconPreference.setChecked(false);
        }

        binding.dynamicTileIconPreference.setChecked(preferences.getBoolean("dynamic_tile_icon", true));

        showDialog = true;

        binding.infoInTitlePreference.setChecked(preferences.getBoolean("infoInTitle", false));

        binding.tileStateLayout.setOnClickListener(v -> TileStatePickerDialog.show(requireContext(), inflater, this::updateTileStateDescription));
        updateTileStateDescription();

        binding.tileTextLayout.setOnClickListener(v -> TileTextDisambiguationDialog.show(requireContext(), inflater));

        return binding.getRoot();
    }

    private void updateTileStateDescription() {
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        if (preferences.getBoolean("emulatePowerSaveTile", false) || preferences.getBoolean("tappableTileEnabled", false)) {
            binding.tileStateLayout.setDescription(R.string.bottom_sheet_preferences_tile_state_disabled_reason);
            return;
        }

        switch (preferences.getInt("tileState", 0)) {
            case 0 -> binding.tileStateLayout.setDescription(R.string.tile_state_always_on);
            case 1 -> binding.tileStateLayout.setDescription(R.string.tile_state_on_when_charging);
            case 2 -> binding.tileStateLayout.setDescription(R.string.tile_state_always_off);
        }
    }

    private void setTileStatePreferenceEnabled(boolean enabled) {
        binding.tileStateLayout.setEnabled(enabled);
        updateTileStateDescription();
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
