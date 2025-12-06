package com.cominatyou.batterytile.standalone;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.cominatyou.batterytile.standalone.preferences.TileTextFormatter;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class QuickSettingsTileService extends TileService {
    private boolean isTappableTileEnabled = false;
    private boolean shouldEmulatePowerSaveTile = false;
    private boolean isCharging = false;
    private boolean hasTimerBeenScheduled = false;
    private final Timer updateTask = new Timer();

    private void setActiveLabelText(String text) {
        if (getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("infoInTitle", false)) {
            getQsTile().setLabel(text);
            getQsTile().setSubtitle(getString(R.string.battery_tile_label));
        }
        else {
            getQsTile().setSubtitle(text);
        }
    }

    private void setBatteryInfo(Intent intent) {
        final int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int plugState = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        final int batteryState = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        final boolean isPluggedIn = plugState == BatteryManager.BATTERY_PLUGGED_AC || plugState == BatteryManager.BATTERY_PLUGGED_USB || plugState == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        final boolean isFullyCharged = isPluggedIn && batteryState == BatteryManager.BATTERY_STATUS_FULL;
        isCharging = batteryState == BatteryManager.BATTERY_STATUS_CHARGING;

        if (isTappableTileEnabled) {
            getQsTile().setState(isCharging ? Tile.STATE_INACTIVE : (getSystemService(PowerManager.class).isPowerSaveMode() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE));
        }

        if (getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("percentage_as_icon", false)) {
            @SuppressLint("DiscouragedApi") final int iconId = getResources().getIdentifier("ic_tile_percent_" + batteryLevel, "drawable", getPackageName());
            getQsTile().setIcon(Icon.createWithResource(this, iconId == 0 ? R.drawable.ic_qs_battery : iconId));
        }
        else if (isPluggedIn && getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("dynamic_tile_icon", true)) {
            switch (plugState) {
                case BatteryManager.BATTERY_PLUGGED_AC -> getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_power));
                case BatteryManager.BATTERY_PLUGGED_USB -> getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_usb));
                case BatteryManager.BATTERY_PLUGGED_WIRELESS -> getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_dock));
                default -> getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_qs_battery));
            }
        }

        if (isFullyCharged) {
            final String customTileText = getSharedPreferences("preferences", MODE_PRIVATE).getString("charging_text", "");
            setActiveLabelText(customTileText.isEmpty() ? getString(R.string.fully_charged) : new TileTextFormatter(this).format(customTileText));
            if (!isTappableTileEnabled) getQsTile().setState(getTileState(true));
        }
        else if (isCharging) {
            final String customTileText = getSharedPreferences("preferences", MODE_PRIVATE).getString("charging_text", "");
            if (!isTappableTileEnabled) getQsTile().setState(getTileState(true));

            if (!customTileText.isEmpty()) {
                setActiveLabelText(new TileTextFormatter(this).format(customTileText));
            }
            else {
                final long remainingTime = getSystemService(BatteryManager.class).computeChargeTimeRemaining();

                // computeChargeTimeRemaining() returns 0 at times for some reason, so check for < 1, not -1
                if (remainingTime < 1) {
                    setActiveLabelText(getString(R.string.charging_no_time_estimate, batteryLevel));
                }
                else if (remainingTime <= 60000) {
                    // case for when less than 1m is remaining - duration returns 0 minutes if less than 1m which is undesirable
                    setActiveLabelText(getString(R.string.charging_less_than_one_hour_left, batteryLevel, 1));
                }
                else {
                    Duration duration = Duration.ofMillis(remainingTime);
                    final long hours = duration.toHours();
                    final long minutes = duration.minusHours(hours).toMinutes();

                    if (hours > 0) {
                        setActiveLabelText(getString(R.string.charging_more_than_one_hour_left, batteryLevel, hours, minutes));
                    }
                    else {
                        setActiveLabelText(getString(R.string.charging_less_than_one_hour_left, batteryLevel, minutes));
                    }
                }
            }
        }
        else {
            final String customTileText = getSharedPreferences("preferences", MODE_PRIVATE).getString("discharging_text", "");
            setActiveLabelText(customTileText.isEmpty() ? batteryLevel + "%" : new TileTextFormatter(this).format(customTileText));
            if (!isTappableTileEnabled) getQsTile().setState(getTileState(false));

            if (getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("percentage_as_icon", false)) {
                @SuppressLint("DiscouragedApi") final int iconId = getResources().getIdentifier("ic_tile_percent_" + batteryLevel, "drawable", getPackageName());
                getQsTile().setIcon(Icon.createWithResource(this, iconId == 0 ? R.drawable.ic_qs_battery : iconId));
            }
            else {
                getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_qs_battery));
            }
        }

        getQsTile().updateTile();
    }

    private void setPowerSaveInfo() {
        final boolean shouldEmulatePowerSaveTile = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("emulatePowerSaveTile", false);

        if (getSystemService(PowerManager.class).isPowerSaveMode()) {
            getQsTile().setState(Tile.STATE_ACTIVE);
            if (shouldEmulatePowerSaveTile) getQsTile().setSubtitle(getString(R.string.power_saver_tile_on_subtitle));
        }
        else {
            getQsTile().setState(Tile.STATE_INACTIVE);
            if (shouldEmulatePowerSaveTile) getQsTile().setSubtitle(getString(R.string.power_saver_tile_off_subtitle));
        }

        getQsTile().updateTile();
    }

    BroadcastReceiver batteryStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setBatteryInfo(intent);
        }
    };

    BroadcastReceiver powerSaveModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setPowerSaveInfo();
        }
    };

    @Override
    public void onStartListening() {
        shouldEmulatePowerSaveTile = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("emulatePowerSaveTile", false);
        isTappableTileEnabled = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("tappableTileEnabled", false);

        final Intent batteryChangedIntent = registerReceiver(batteryStateReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        assert batteryChangedIntent != null;
        final int status = batteryChangedIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        if (shouldEmulatePowerSaveTile) {
            unregisterReceiver(batteryStateReceiver);
            registerReceiver(powerSaveModeReceiver, new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED));
            getQsTile().setLabel(getString(R.string.power_save_tile_label));
            getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_battery_saver));

            if (isCharging) {
                getQsTile().setState(Tile.STATE_UNAVAILABLE);
                getQsTile().setSubtitle(getString(R.string.power_save_tile_unavailable_subtitle));
                getQsTile().updateTile();
            }
            else {
                setPowerSaveInfo();
            }
        }
        else {
            getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_qs_battery));
            getQsTile().setLabel(getString(R.string.battery_tile_label));

            if (!isTappableTileEnabled) {
                getQsTile().setState(getTileState(isCharging));
            }
            else {
                final IntentFilter powerSaveChangedFilter = new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
                registerReceiver(powerSaveModeReceiver, powerSaveChangedFilter);
                setPowerSaveInfo();
            }

            setBatteryInfo(batteryChangedIntent);
        }

        if (!hasTimerBeenScheduled && getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("live_tile_updates", false)) {
            updateTask.schedule(new TimerTask() {
                public void run() {
                    final Intent batteryStateIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    assert batteryStateIntent != null;

                    setBatteryInfo(batteryStateIntent);
                }
            }, 2000, 2000);

            hasTimerBeenScheduled = true;
        }
    }

    private int getTileState(boolean isCharging) {
        return switch (getSharedPreferences("preferences", MODE_PRIVATE).getInt("tileState", 0)) {
            case 0 -> Tile.STATE_ACTIVE;
            case 1 -> isCharging ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
            default -> Tile.STATE_INACTIVE;
        };
    }

    @Override
    public void onClick() {
        super.onClick();
        if (!isTappableTileEnabled || isCharging) return;

        final boolean isInPowerSaveMode = getSystemService(PowerManager.class).isPowerSaveMode();

        Settings.Global.putInt(getContentResolver(), "low_power", isInPowerSaveMode ? 0 : 1);
        getQsTile().setState(isInPowerSaveMode ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        if (isTappableTileEnabled) {
            unregisterReceiver(powerSaveModeReceiver);
        }
        if (!shouldEmulatePowerSaveTile) {
            updateTask.cancel();
            updateTask.purge();

            unregisterReceiver(batteryStateReceiver);
        }
    }
}
