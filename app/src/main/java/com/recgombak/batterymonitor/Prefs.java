package com.recgombak.batterymonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import java.util.UUID;

public final class Prefs {
 private static final String FILE="rec_battery_prefs";
 private Prefs(){}

 public static SharedPreferences sp(Context c){return c.getSharedPreferences(FILE,Context.MODE_PRIVATE);}

 public static String getId(Context c){
  SharedPreferences p=sp(c);String id=p.getString("device_id",null);
  if(id==null){String a=Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);id=(a!=null&&!a.isEmpty())?a:UUID.randomUUID().toString();p.edit().putString("device_id",id).apply();}
  return id;
 }

 public static String getName(Context c){
  SharedPreferences p=sp(c);String n=p.getString("device_name",null);
  if(n==null||n.trim().isEmpty()){String s=getId(c);s=s.substring(Math.max(0,s.length()-4)).toUpperCase();n=Build.MODEL+"-"+s;p.edit().putString("device_name",n).apply();}
  return n;
 }
 public static void setName(Context c,String n){sp(c).edit().putString("device_name",n.trim()).apply();}

 public static boolean isEnabled(Context c){return sp(c).getBoolean("service_enabled",true);}
 public static void setEnabled(Context c,boolean v){sp(c).edit().putBoolean("service_enabled",v).apply();}

 public static String getRole(Context c){return sp(c).getString("role","");}
 public static void setRole(Context c,String v){sp(c).edit().putString("role",v).apply();}

 public static int getThreshold(Context c){return sp(c).getInt("threshold",65);}
 public static void setThreshold(Context c,int v){sp(c).edit().putInt("threshold",Math.max(1,Math.min(100,v))).apply();}

 public static int getChargeThreshold(Context c){return sp(c).getInt("charge_threshold",95);}
 public static void setChargeThreshold(Context c,int v){sp(c).edit().putInt("charge_threshold",Math.max(1,Math.min(100,v))).apply();}
}
