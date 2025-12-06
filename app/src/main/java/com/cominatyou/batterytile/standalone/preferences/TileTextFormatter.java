package com.cominatyou.batterytile.standalone.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.HashMap;

/**
 * Class for user-configurable scanf-style formatting of tile text.
 */
public class TileTextFormatter {
    private final HashMap<String, Object> formatters = new HashMap<>();

    public TileTextFormatter(Context context) {
        final BatteryManager bm = context.getSystemService(BatteryManager.class);
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        final DecimalFormat numberFormat = new DecimalFormat("#.##");
        assert batteryIntent != null;

        final int instantaneousCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        final int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0); // provided as mV

        // CURRENT_NOW and CURRENT_AVERAGE are positive when charging and negative when discharging
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            // Samsung devices, for some reason, returns this in milli-amperes instead of microamperes like the Android documentation specifies
            formatters.put("c", instantaneousCurrent);
            formatters.put("a", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
        }
        else {
            // Android provides these as microamperes, so divide by 1000 to convert to mA
            formatters.put("c", instantaneousCurrent / 1000);
            formatters.put("a", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE) / 1000);
        }

        // Temperature of the battery
        formatters.put("t", batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f); // Celsius
        formatters.put("f", (int) ((double) batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 * 1.8 + 32)); // Fahrenheit

        // Voltage of the battery - provided as mV by Android, so divide by 1000 to get volts
        formatters.put("v", numberFormat.format(voltage / 1000f));

        // Wattage of the battery - Power = I * V (divide instantaneousCurrent by 1,000,000 to get amps)
        formatters.put("w", numberFormat.format(Math.abs(instantaneousCurrent / 1000000f * voltage / 1000f)));

        // Battery level
        formatters.put("l", batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));

        // Remaining charge time
        final long remainingTime = context.getSystemService(BatteryManager.class).computeChargeTimeRemaining();

        // I haven't implemented restrictions to ensure that this is only used when charging
        // so just set the values to 0 if we're not charging
        if (remainingTime == -1) {
            formatters.put("h", "0");
            formatters.put("m", "0");
        }
        else {
            final Duration duration = Duration.ofMillis(remainingTime);
            final long hours = duration.toHours();
            final long minutes = duration.minusHours(hours).toMinutes();

            formatters.put("h", hours);
            formatters.put("m", minutes);
        }
    }

    public String format(String format) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < format.length(); i++) {
            if (format.charAt(i) == '%') {
                if (i + 1 < format.length()) {
                    final String formatter = format.substring(i + 1, i + 2);
                    if (formatters.containsKey(formatter)) {
                        formatted.append(formatters.get(formatter));
                        i++;
                    }
                    else {
                        formatted.append('%');
                    }
                }
                else {
                    formatted.append('%');
                }
            }
            else {
                formatted.append(format.charAt(i));
            }
        }

        return formatted.toString();
    }
}
