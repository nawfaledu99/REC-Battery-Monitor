package com.recgombak.batterymonitor;
import android.content.BroadcastReceiver; import android.content.Context; import android.content.Intent; import android.os.Build;
public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context, Intent intent) { if (!Prefs.isEnabled(context)) return; Intent service = new Intent(context, BatteryBroadcastService.class); try { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(service); else context.startService(service); } catch (Exception ignored) {} }
}
