package com.recgombak.batterymonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import java.util.UUID;

public final class Prefs {
    private static final String FILE = "rec_battery_prefs";
    private static final String KEY_ID = "device_id";
    private static final String KEY_NAME = "device_name";
    private static final String KEY_ENABLED = "service_enabled";
    private Prefs() {}
    public static SharedPreferences sp(Context c) { return c.getSharedPreferences(FILE, Context.MODE_PRIVATE); }
    public static String getId(Context c) {
        SharedPreferences p = sp(c); String id = p.getString(KEY_ID, null);
        if (id == null) { String androidId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID); id = (androidId != null && !androidId.isEmpty()) ? androidId : UUID.randomUUID().toString(); p.edit().putString(KEY_ID, id).apply(); }
        return id;
    }
    public static String getName(Context c) {
        SharedPreferences p = sp(c); String name = p.getString(KEY_NAME, null);
        if (name == null || name.trim().isEmpty()) { String suffix = getId(c); suffix = suffix.substring(Math.max(0, suffix.length()-4)).toUpperCase(); name = Build.MODEL + "-" + suffix; p.edit().putString(KEY_NAME, name).apply(); }
        return name;
    }
    public static void setName(Context c, String name) { sp(c).edit().putString(KEY_NAME, name.trim()).apply(); }
    public static boolean isEnabled(Context c) { return sp(c).getBoolean(KEY_ENABLED, true); }
    public static void setEnabled(Context c, boolean enabled) { sp(c).edit().putBoolean(KEY_ENABLED, enabled).apply(); }
}
