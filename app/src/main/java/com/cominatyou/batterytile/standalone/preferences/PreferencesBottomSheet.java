package com.cominatyou.batterytile.standalone.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.cominatyou.batterytile.standalone.databinding.BottomSheetPreferencesBinding;
import com.cominatyou.batterytile.standalone.debug.DebugDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class PreferencesBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetPreferencesBinding binding;
    boolean showDialog = false;

    private void forceTappableTile(boolean force) {
        binding.tappableTileSwitch.setChecked(force);
        binding.tappableTileSwitch.setEnabled(!force);
        binding.tappableTileTitle.setAlpha(force ? 0.4f : 1);
        binding.tappableTileDescription.setAlpha(force ? 0.4f : 1);
        binding.tappableTileLayout.setEnabled(!force);
    }

    private boolean checkIfPermissionIsDenied() {
        return requireContext().checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = BottomSheetPreferencesBinding.inflate(inflater, container, false);
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        binding.preferencesBottomSheetTitle.setOnLongClickListener(l -> {
            DebugDialog.show(requireContext());
            return true;
        });

        binding.tappableTileLayout.setOnClickListener(self -> binding.tappableTileSwitch.toggle());

        binding.tappableTileSwitch.setOnCheckedChangeListener((self, state) -> {
            if (checkIfPermissionIsDenied() && state) {
                self.setChecked(false);
                AdbDialog.show(requireContext());
            }
            else {
                if (state && showDialog) {
                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.power_save_tile_warning_title)
                            .setMessage(R.string.power_save_system_warning_description)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

                    final AlertDialog dialog = builder.show();

                    ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gs_flex));
                    @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier("alertTitle", "id", requireContext().getPackageName());
                    ((TextView) Objects.requireNonNull(dialog.findViewById(resId))).setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gs_flex));
                    ((Button) Objects.requireNonNull(dialog.findViewById(android.R.id.button1))).setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gs_flex));
                }
                preferences
                        .edit()
                        .putBoolean("tappableTileEnabled", state)
                        .apply();

                setTileStatePreferenceEnabled(!state);
            }
        });

        binding.emulatePowerSaveTilePreferenceLayout.setOnClickListener(self -> binding.emulatePowerSaveTilePreferenceSwitch.toggle());

        binding.emulatePowerSaveTilePreferenceSwitch.setOnCheckedChangeListener((self, state) -> {
            if (checkIfPermissionIsDenied() && state) {
                self.setChecked(false);
                AdbDialog.show(requireContext());
            }
            else {
                forceTappableTile(state);
                preferences.edit().putBoolean("emulatePowerSaveTile", state).apply();
                setTileStatePreferenceEnabled(!state);

                binding.tileTextLayout.setEnabled(!state);
                binding.tileTextTitle.setAlpha(state ? 0.4f : 1);
                binding.tileTextDescription.setAlpha(state ? 0.4f : 1);
                binding.tileTextDescription.setText(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_tile_text_description));

                binding.infoInTitlePreferenceLayout.setEnabled(!state);
                binding.infoInTitlePreferenceTitle.setAlpha(state ? 0.4f : 1);
                binding.infoInTitlePreferenceDescription.setAlpha(state ? 0.4f : 1);
                binding.infoInTitlePreferenceDescription.setText(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.info_in_title_option_description));
                binding.infoInTitleSwitch.setChecked(false);
                binding.infoInTitleSwitch.setEnabled(!state);

                binding.percentageAsTileIconSwitch.setEnabled(!state);
                binding.percentageAsTileIconLayout.setEnabled(!state);
                binding.dynamicTileIconSwitch.setChecked(false);
                binding.percentageAsTileIconTitle.setAlpha(state ? 0.4f : 1);
                binding.percentageAsTileIconDescription.setAlpha(state ? 0.4f : 1);
                binding.percentageAsTileIconDescription.setText(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_percentage_as_tile_icon_description));

                binding.dynamicTileIconLayout.setEnabled(!state);
                binding.dynamicTileIconTitle.setAlpha(state ? 0.4f : 1);
                binding.dynamicTileIconDescription.setAlpha(state ? 0.4f : 1);
                binding.dynamicTileIconDescription.setText(getString(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_dynamic_tile_icon_description));
                binding.dynamicTileIconSwitch.setChecked(false);
                binding.dynamicTileIconSwitch.setEnabled(!state);
            }
        });

        binding.infoInTitlePreferenceLayout.setOnClickListener(self -> binding.infoInTitleSwitch.toggle());
        binding.infoInTitleSwitch.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("infoInTitle", state).apply());

        binding.percentageAsTileIconLayout.setOnClickListener(self -> binding.percentageAsTileIconSwitch.toggle());

        binding.percentageAsTileIconSwitch.setOnCheckedChangeListener((self, state) -> {
            binding.dynamicTileIconSwitch.setChecked(false);
            binding.dynamicTileIconSwitch.setEnabled(!state);
            binding.dynamicTileIconLayout.setEnabled(!state);
            binding.dynamicTileIconTitle.setAlpha(state ? 0.4f : 1);
            binding.dynamicTileIconDescription.setAlpha(state ? 0.4f : 1);
            binding.dynamicTileIconDescription.setText(state ? R.string.bottom_sheet_preferences_tile_state_disabled_reason : R.string.bottom_sheet_preferences_dynamic_tile_icon_description);

            preferences.edit().putBoolean("percentage_as_icon", state).apply();
        });

        binding.dynamicTileIconLayout.setOnClickListener(self -> binding.dynamicTileIconSwitch.toggle());
        binding.dynamicTileIconSwitch.setOnCheckedChangeListener((self, state) -> preferences.edit().putBoolean("dynamic_tile_icon", state).apply());

        if (preferences.getBoolean("emulatePowerSaveTile", false)) {
            binding.emulatePowerSaveTilePreferenceSwitch.setChecked(true);
            forceTappableTile(true);
        }
        else {
            binding.tappableTileSwitch.setChecked(preferences.getBoolean("tappableTileEnabled", false));
        }

        if (preferences.getBoolean("percentage_as_icon", false)) {
            binding.percentageAsTileIconSwitch.setChecked(true);
            binding.dynamicTileIconSwitch.setChecked(false);
            binding.dynamicTileIconSwitch.setEnabled(false);
            binding.dynamicTileIconLayout.setEnabled(false);
            binding.dynamicTileIconTitle.setAlpha(0.4f);
            binding.dynamicTileIconDescription.setAlpha(0.4f);
            binding.dynamicTileIconDescription.setText(R.string.bottom_sheet_preferences_tile_state_disabled_reason);
        }

        binding.dynamicTileIconSwitch.setChecked(preferences.getBoolean("dynamic_tile_icon", true));

        showDialog = true;

        binding.infoInTitleSwitch.setChecked(preferences.getBoolean("infoInTitle", false));

        binding.tileStateLayout.setOnClickListener(v -> TileStatePickerDialog.show(requireContext(), this::updateTileStateDescription));
        updateTileStateDescription();

        binding.tileTextLayout.setOnClickListener(v -> TileTextDisambiguationDialog.show(requireContext()));

        return binding.getRoot();
    }

    private void updateTileStateDescription() {
        final SharedPreferences preferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        if (preferences.getBoolean("emulatePowerSaveTile", false) || preferences.getBoolean("tappableTileEnabled", false)) {
            binding.tileStateDescription.setText(R.string.bottom_sheet_preferences_tile_state_disabled_reason);
            return;
        }

        switch (preferences.getInt("tileState", 0)) {
            case 0 -> binding.tileStateDescription.setText(R.string.tile_state_always_on);
            case 1 -> binding.tileStateDescription.setText(R.string.tile_state_on_when_charging);
            case 2 -> binding.tileStateDescription.setText(R.string.tile_state_always_off);
        }
    }

    private void setTileStatePreferenceEnabled(boolean enabled) {
        binding.tileStateTitle.setAlpha(enabled ? 1 : 0.4f);
        binding.tileStateDescription.setAlpha(enabled ? 1 : 0.4f);
        binding.tileStateLayout.setEnabled(enabled);
        updateTileStateDescription();
    }

    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        requireActivity().finish();
    }
}
