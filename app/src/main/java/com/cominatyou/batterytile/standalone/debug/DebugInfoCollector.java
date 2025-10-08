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
    @SuppressLint({"DefaultLocale"})
    protected void collectDebugInfo(final Context context, final AlertDialog dialog, final ExecutorService executorService) {
        final StringBuilder debugInfo = new StringBuilder();

        appendStaticInfo(context, debugInfo);
        appendTileInfo(context, debugInfo);

        final Intent initialBatteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (initialBatteryIntent == null) {
            System.err.println("Battery intent was null, aborting debug collection.");
            context.getMainExecutor().execute(dialog::dismiss);
            return;
        }

        final int status = initialBatteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        final boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        collectBatteryInfoPasses(context, dialog, debugInfo, isCharging);

        if (!isCharging) {
            context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_connect_to_power_description)));

            final BroadcastReceiver chargingStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctx, Intent intent) {
                    final int newStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    if (newStatus == BatteryManager.BATTERY_STATUS_CHARGING || newStatus == BatteryManager.BATTERY_STATUS_FULL) {
                        context.unregisterReceiver(this);
                        // Use the provided executor to run the second collection pass off the main thread.
                        executorService.submit(() -> {
                            collectBatteryInfoPasses(context, dialog, debugInfo, true);
                            shareReport(context, dialog, debugInfo.toString());
                        });
                    }
                }
            };
            context.registerReceiver(chargingStateReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } else {
            context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_disconnect_from_power_description)));
            final BroadcastReceiver dischargingStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctx, Intent intent) {
                    final int newStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    if (newStatus != BatteryManager.BATTERY_STATUS_CHARGING && newStatus != BatteryManager.BATTERY_STATUS_FULL) {
                        context.unregisterReceiver(this);
                        executorService.submit(() -> {
                            collectBatteryInfoPasses(context, dialog, debugInfo, false);
                            shareReport(context, dialog, debugInfo.toString());
                        });
                    }
                }
            };
            context.registerReceiver(dischargingStateReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }

    private static void collectBatteryInfoPasses(Context context, AlertDialog dialog, StringBuilder debugInfo, boolean isCharging) {
        for (int i = 1; i <= 3; i++) {
            final int passNumber = i;
            context.getMainExecutor().execute(() -> dialog.setMessage(context.getString(R.string.debug_dialog_collecting_info_description, passNumber)));

            String infoBlock = getBatteryInfo(context, passNumber, isCharging);
            debugInfo.append(infoBlock);

            // Don't sleep after the final pass.
            if (i < 3) {
                try {
                    Thread.sleep(10000);
                } catch (final InterruptedException e) {
                    // If the thread is interrupted, restore the interrupted status and stop collecting.
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static String getBatteryInfo(final Context context, final int passNumber, final boolean isCharging) {
        final BatteryManager bm = context.getSystemService(BatteryManager.class);
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (batteryIntent == null) return "\n\nUnable to retrieve battery information (intent was null)";

        final int instantaneousCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        final int averageCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        final int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        final float temperatureCelsius = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f;
        final int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        final long remainingChargeTime = bm.computeChargeTimeRemaining();

        final String powerSource = switch (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            case BatteryManager.BATTERY_PLUGGED_AC -> "AC";
            case BatteryManager.BATTERY_PLUGGED_USB -> "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless";
            default -> "Unplugged";
        };

        final String chargeStatus = isCharging ? "Charging" : "Discharging";
        StringBuilder batteryInfo = new StringBuilder()
                .append("\n--- Battery Info (Pass ").append(passNumber).append(", ").append(chargeStatus).append(") ---\n")
                .append("Instantaneous Current: ").append(instantaneousCurrent).append(" µA\n")
                .append("Average Current: ").append(averageCurrent).append(" µA\n")
                .append("Voltage: ").append(voltage).append(" mV\n")
                .append("Temperature: ").append(temperatureCelsius).append(" °C\n")
                .append("Battery Level: ").append(level).append("%\n")
                .append("Remaining Charge Time: ").append(remainingChargeTime == -1 ? "Unknown" : remainingChargeTime + "ms").append("\n");

        if (isCharging) {
            batteryInfo.append("Power Source Type: ").append(powerSource).append("\n");
        }

        return batteryInfo.toString();
    }

    private static void shareReport(final Context context, AlertDialog dialog, String report) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Battery Tile Debug Report");
        intent.putExtra(Intent.EXTRA_TEXT, report);

        context.startActivity(Intent.createChooser(intent, null));
        context.getMainExecutor().execute(dialog::dismiss);
    }

    @SuppressLint("DefaultLocale")
    private static void appendStaticInfo(final Context context, StringBuilder debugInfo) {
        debugInfo
                .append("--- Battery Tile Debug Report ---\n")
                .append(String.format("Report compiled on %s\n", java.time.ZonedDateTime.now().toString()))
                .append(String.format("App Version: %s (%s)\n", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                .append("App Variant: Standalone")
                .append("\n\n--- Device Info ---\n")
                .append(String.format("Device: %s %s (%s)\n", android.os.Build.MANUFACTURER, android.os.Build.DEVICE, android.os.Build.MODEL))
                .append(String.format("Android Version: %s (SDK %d)\n", android.os.Build.VERSION.RELEASE, android.os.Build.VERSION.SDK_INT));

        final boolean hasPermission = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        debugInfo.append(String.format("WRITE_SECURE_SETTINGS granted: %s", hasPermission ? "Yes" : "No"));
    }

    private static void appendTileInfo(Context context, StringBuilder debugInfo) {
        final SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final String chargingText = prefs.getString("charging_text", "");
        final String dischargingText = prefs.getString("discharging_text", "");

        debugInfo
                .append("\n\n--- Tile Info ---\n")
                .append("Emulating Power Save Tile: ").append(prefs.getBoolean("emulatePowerSaveTile", false) ? "Yes" : "No").append("\n")
                .append("Tile Info in Title: ").append(prefs.getBoolean("infoInTitle", false) ? "Yes" : "No").append("\n")
                .append("Dynamic Tile Icon: ").append(prefs.getBoolean("dynamic_tile_icon", false) ? "Yes" : "No").append("\n")
                .append("Tappable Tile: ").append(prefs.getBoolean("tappableTileEnabled", false) ? "Yes" : "No").append("\n")
                .append("Tile State: ").append(prefs.getInt("tileState", 0)).append("\n")
                .append("Charging Text: ").append(chargingText.isEmpty() ? "null" : chargingText).append("\n")
                .append("Discharging Text: ").append(dischargingText.isEmpty() ? "null" : dischargingText).append("\n");
    }
}