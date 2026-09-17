package com.recgombak.batterymonitor;
import android.content.BroadcastReceiver; import android.content.Context; import android.content.Intent; import android.os.Build;
public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context, Intent intent) { if (!Prefs.isEnabled(context)) return; start(context,new Intent(context,BatteryBroadcastService.class)); if("tablet".equals(Prefs.getRole(context))) start(context,new Intent(context,LowBatteryVoiceService.class)); if("monitor".equals(Prefs.getRole(context))) start(context,new Intent(context,ConfigAckService.class)); }
 private void start(Context context,Intent service){try{if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)context.startForegroundService(service);else context.startService(service);}catch(Exception ignored){}}
}
