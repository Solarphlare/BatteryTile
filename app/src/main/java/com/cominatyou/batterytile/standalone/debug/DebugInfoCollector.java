package com.cominatyou.batterytile.standalone.debug;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;

import androidx.appcompat.app.AlertDialog;

import com.cominatyou.batterytile.standalone.BuildConfig;
import com.cominatyou.batterytile.standalone.R;

import java.util.concurrent.ExecutorService;

public class DebugInfoCollector {
    final StringBuilder debugInfo = new StringBuilder();
    private String getBatteryInfo(final Context context, final int passNumber, final boolean isCharging) {
        final BatteryManager bm = context.getSystemService(BatteryManager.class);
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (batteryIntent == null) return "Unable to retrieve battery information (intent was null)";

        final int instantaneousCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        final int averageCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);

        final int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0); // provided as mV
        final float temperatureCelsius = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f;

        final int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        final long remainingChargeTime = bm.computeChargeTimeRemaining();

        final String powerSource = switch (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            case BatteryManager.BATTERY_PLUGGED_AC -> "AC";
            case BatteryManager.BATTERY_PLUGGED_USB -> "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless";
            default -> "Unplugged";
        };

        String batteryInfo = "\n--- Battery Info (Pass " + passNumber + ", " + (isCharging ? "Charging" : "Discharging") +  ")" + " ---\n" +
                "Instantaneous Current: " + instantaneousCurrent + " µA\n" +
                "Average Current: " + averageCurrent + " µA\n" +
                "Voltage: " + voltage + " mV\n" +
                "Temperature: " + temperatureCelsius + " °C\n" +
                "Battery Level: " + level + "%\n" +
                "Remaining Charge Time: " + (remainingChargeTime == -1 ? "Unknown" : remainingChargeTime + " ms") + "\n";

        if (isCharging) {
            batteryInfo += "Power Source Type: " + powerSource + "\n";
        }

        return batteryInfo;
    }

    private void collectSecondBlockInfo(final Context context, final AlertDialog dialog, final boolean isCharging) {
        context.getMainExecutor().execute((() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, 1))));
        String secondInfoBlock = getBatteryInfo(context, 1, isCharging);
        debugInfo.append(secondInfoBlock);

        try {
            Thread.sleep(10000);
        }
        catch (final InterruptedException ignored) {
            return;
        }

        context.getMainExecutor().execute((() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, 2))));
        secondInfoBlock = getBatteryInfo(context, 2, isCharging);
        debugInfo.append(secondInfoBlock);

        try {
            Thread.sleep(10000);
        }
        catch (final InterruptedException ignored) {
            return;
        }

        context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, 3)));
        secondInfoBlock = getBatteryInfo(context, 3, isCharging);
        debugInfo.append(secondInfoBlock);

        // i was going to send this as a text/plain file.
        // however, sharing a file on android is absolutely impossible
        // because of the whole "FileProvider" thing
        // so this works fine.
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Battery Tile Debug Report");
        intent.putExtra(Intent.EXTRA_TEXT, debugInfo.toString());

        context.startActivity(Intent.createChooser(intent, null));
        context.getMainExecutor().execute(dialog::dismiss);
    }

    @SuppressLint("DefaultLocale")
    protected void collectDebugInfo(final Context context, final AlertDialog dialog, final ExecutorService executorService) {
        debugInfo
                .append("--- Battery Tile Debug Report ---\n")
                .append(String.format("Report compiled on %s\n", java.time.ZonedDateTime.now().toString()))
                .append(String.format("App Version: %s (%s)\n", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                .append("App Variant: Standalone")
                .append("\n\n--- Device Info ---\n")
                .append(String.format("Device: %s %s (%s)\n", android.os.Build.MANUFACTURER, android.os.Build.DEVICE, android.os.Build.MODEL))
                .append(String.format("Android Version: %s (SDK %d)\n", android.os.Build.VERSION.RELEASE, android.os.Build.VERSION.SDK_INT));

        final boolean hasWriteSecureSettingsPermission = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        debugInfo.append(String.format("WRITE_SECURE_SETTINGS granted: %s", hasWriteSecureSettingsPermission ? "Yes" : "No"));

        final SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final boolean isEmulatingPowerSaveTile = preferences.getBoolean("emulatePowerSaveTile", false);
        final boolean hasTileInfoInTitle = preferences.getBoolean("infoInTitle", false);
        final boolean isUsingDynamicTileIcon = preferences.getBoolean("dynamic_tile_icon", false);
        final boolean isTappableTile = preferences.getBoolean("tappableTileEnabled", false);
        final int tileState = preferences.getInt("tileState", 0);
        final String customChargingText = preferences.getString("charging_text", "null");
        final String customDischargingText = preferences.getString("discharging_text", "null");

        debugInfo
                .append("\n\n--- Tile Info ---\n")
                .append("Emulating Power Save Tile: ").append(isEmulatingPowerSaveTile ? "Yes" : "No").append("\n")
                .append("Tile Info in Title: ").append(hasTileInfoInTitle ? "Yes" : "No").append("\n")
                .append("Dynamic Tile Icon: ").append(isUsingDynamicTileIcon ? "Yes" : "No").append("\n")
                .append("Tappable Tile: ").append(isTappableTile ? "Yes" : "No").append("\n")
                .append("Tile State: ").append(tileState).append("\n")
                .append("Charging Text: ").append(customChargingText.isEmpty() ? "null" : customChargingText).append("\n")
                .append("Discharging Text: ").append(customDischargingText.isEmpty() ? "null" : customDischargingText).append("\n");


        context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, 1)));

        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            System.err.println("First attery intent was null");
            return;
        }

        final int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        final boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        String firstInfoBlock = getBatteryInfo(context, 1, false);
        debugInfo.append(firstInfoBlock);

        try {
            Thread.sleep(10000);
        }
        catch (final InterruptedException ignored) {
            return;
        }

        context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, 2)));
        firstInfoBlock = getBatteryInfo(context, 2, false);
        debugInfo.append(firstInfoBlock);

        try {
            Thread.sleep(10000);
        }
        catch (final InterruptedException ignored) {
            return;
        }

        context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, 3)));
        firstInfoBlock = getBatteryInfo(context, 3, false);
        debugInfo.append(firstInfoBlock);

        // wait for a change in power state
        if (!isCharging) {
            context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_connect_to_power_description)));

            final BroadcastReceiver chargingStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctx, Intent intent) {
                    final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
                        context.unregisterReceiver(this);
                        executorService.submit(() -> collectSecondBlockInfo(context, dialog, true));
                    }
                }
            };

            context.registerReceiver(chargingStateReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        else {
            context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_disconnect_from_power_description)));
            final BroadcastReceiver dischargingStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctx, Intent intent) {
                    final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING && status != BatteryManager.BATTERY_STATUS_FULL) {
                        context.unregisterReceiver(this);
                        executorService.submit(() -> collectSecondBlockInfo(context, dialog, false));
                    }
                }
            };

            context.registerReceiver(dischargingStateReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }
}
