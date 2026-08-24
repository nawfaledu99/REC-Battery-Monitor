package com.recgombak.batterymonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Prefs {
    private static final String FILE="rec_battery_prefs";
    private Prefs(){}
    public static SharedPreferences sp(Context c){return c.getSharedPreferences(FILE,Context.MODE_PRIVATE);}
    public static String getId(Context c){SharedPreferences p=sp(c);String id=p.getString("device_id",null);if(id==null){String a=Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);id=(a!=null&&!a.isEmpty())?a:UUID.randomUUID().toString();p.edit().putString("device_id",id).apply();}return id;}
    public static String getName(Context c){SharedPreferences p=sp(c);String n=p.getString("device_name",null);if(n==null||n.trim().isEmpty()){String s=getId(c);s=s.substring(Math.max(0,s.length()-4)).toUpperCase();n=Build.MODEL+"-"+s;p.edit().putString("device_name",n).apply();}return n;}
    public static void setName(Context c,String n){sp(c).edit().putString("device_name",n.trim()).apply();}
    public static boolean isEnabled(Context c){return sp(c).getBoolean("service_enabled",true);}
    public static void setEnabled(Context c,boolean v){sp(c).edit().putBoolean("service_enabled",v).apply();}
    public static String getRole(Context c){return sp(c).getString("role","");}
    public static void setRole(Context c,String v){sp(c).edit().putString("role",v).apply();}
    public static int getThreshold(Context c){return sp(c).getInt("threshold",65);}
    public static void setThreshold(Context c,int v){sp(c).edit().putInt("threshold",Math.max(1,Math.min(100,v))).apply();}
    public static int getInterval(Context c){return sp(c).getInt("interval",15);}
    public static void setInterval(Context c,int v){sp(c).edit().putInt("interval",Math.max(5,Math.min(3600,v))).apply();}
    public static int getAlarmSeconds(Context c){return sp(c).getInt("alarm_seconds",7);}
    public static void setAlarmSeconds(Context c,int v){sp(c).edit().putInt("alarm_seconds",Math.max(1,Math.min(60,v))).apply();}
    public static boolean scheduleOn(Context c){return sp(c).getBoolean("schedule_on",true);}
    public static void setScheduleOn(Context c,boolean v){sp(c).edit().putBoolean("schedule_on",v).apply();}
    public static int getStartMin(Context c){return sp(c).getInt("start_min",7*60+30);}
    public static int getEndMin(Context c){return sp(c).getInt("end_min",19*60);}
    public static void setSchedule(Context c,int start,int end){sp(c).edit().putInt("start_min",start).putInt("end_min",end).apply();}
    public static boolean inSchedule(Context c){if(!scheduleOn(c))return true;Calendar x=Calendar.getInstance();int now=x.get(Calendar.HOUR_OF_DAY)*60+x.get(Calendar.MINUTE);int s=getStartMin(c),e=getEndMin(c);return s<=e?now>=s&&now<e:now>=s||now<e;}
    public static long getTimerEnd(Context c){return sp(c).getLong("timer_end",0L);}
    public static void setTimerEnd(Context c,long t){sp(c).edit().putLong("timer_end",t).apply();}
    public static boolean isTimerAlarmed(Context c){return sp(c).getBoolean("timer_alarmed",false);}
    public static void setTimerAlarmed(Context c,boolean v){sp(c).edit().putBoolean("timer_alarmed",v).apply();}
    public static boolean isTimerDone(Context c){return sp(c).getBoolean("timer_done",false);}
    public static void setTimerDone(Context c,boolean v){sp(c).edit().putBoolean("timer_done",v).apply();}
    public static Set<String> getHidden(Context c){return new HashSet<>(sp(c).getStringSet("hidden_devices",new HashSet<>()));}
    public static void hideDevice(Context c,String id){Set<String>s=getHidden(c);s.add(id);sp(c).edit().putStringSet("hidden_devices",s).apply();}
    public static void clearHidden(Context c){sp(c).edit().remove("hidden_devices").apply();}

    public static synchronized void saveKnownDevices(Context c,Map<String,DeviceInfo> devices){try{JSONArray a=new JSONArray();for(DeviceInfo d:devices.values()){if(d==null||d.id==null||d.id.isEmpty())continue;JSONObject o=new JSONObject();o.put("id",d.id);o.put("name",d.name==null?"Tablet":d.name);o.put("model",d.model==null?"Android":d.model);o.put("ip",d.ip==null?"":d.ip);o.put("battery",d.battery);o.put("charging",d.charging);o.put("lastSeen",d.lastSeen);o.put("timerEnd",d.timerEnd);o.put("timerDone",d.timerDone);a.put(o);}sp(c).edit().putString("known_devices",a.toString()).apply();}catch(Exception ignored){}}
    public static synchronized Map<String,DeviceInfo> loadKnownDevices(Context c){Map<String,DeviceInfo> out=new LinkedHashMap<>();try{String raw=sp(c).getString("known_devices","[]");JSONArray a=new JSONArray(raw);for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o==null)continue;DeviceInfo d=new DeviceInfo();d.id=o.optString("id","");if(d.id.isEmpty())continue;d.name=o.optString("name","Tablet");d.model=o.optString("model","Android");d.ip=o.optString("ip","");d.battery=o.optInt("battery",-1);d.charging=o.optBoolean("charging",false);d.lastSeen=o.optLong("lastSeen",0);d.timerEnd=o.optLong("timerEnd",0);d.timerDone=o.optBoolean("timerDone",false);out.put(d.id,d);}}catch(Exception ignored){}return out;}
    public static synchronized void removeKnownDevice(Context c,String id){Map<String,DeviceInfo> m=loadKnownDevices(c);m.remove(id);saveKnownDevices(c,m);}
}
