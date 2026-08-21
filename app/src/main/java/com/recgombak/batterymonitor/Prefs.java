package com.recgombak.batterymonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import java.util.Calendar;
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
    public static boolean scheduleOn(Context c){return sp(c).getBoolean("schedule_on",true);}
    public static void setScheduleOn(Context c,boolean v){sp(c).edit().putBoolean("schedule_on",v).apply();}
    public static int getStartMin(Context c){return sp(c).getInt("start_min",7*60+30);}
    public static int getEndMin(Context c){return sp(c).getInt("end_min",19*60);}
    public static void setSchedule(Context c,int start,int end){sp(c).edit().putInt("start_min",start).putInt("end_min",end).apply();}
    public static boolean inSchedule(Context c){if(!scheduleOn(c))return true;Calendar x=Calendar.getInstance();int now=x.get(Calendar.HOUR_OF_DAY)*60+x.get(Calendar.MINUTE);int s=getStartMin(c),e=getEndMin(c);return s<=e?now>=s&&now<e:now>=s||now<e;}
    public static long getTimerEnd(Context c){return sp(c).getLong("timer_end",0L);}
    public static void setTimerEnd(Context c,long t){sp(c).edit().putLong("timer_end",t).apply();}
}
