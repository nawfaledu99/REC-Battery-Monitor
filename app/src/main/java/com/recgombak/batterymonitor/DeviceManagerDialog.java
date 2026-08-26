package com.recgombak.batterymonitor;

import android.app.*;import android.content.*;import android.graphics.Color;import android.view.*;import android.widget.*;import java.text.SimpleDateFormat;import java.util.*;

public final class DeviceManagerDialog{
 public interface Callback{void onDeleted(String id);}
 private DeviceManagerDialog(){}
 public static void show(Activity a,Map<String,DeviceInfo> devices,Callback cb){
  ScrollView scroll=new ScrollView(a);LinearLayout box=new LinearLayout(a);box.setOrientation(LinearLayout.VERTICAL);int pad=dp(a,12);box.setPadding(pad,pad,pad,pad);scroll.addView(box);
  TextView note=new TextView(a);note.setText("Fungsi padam disimpan di sini supaya tidak mudah tersalah tekan. Tekan PADAM hanya untuk rekod lama atau tablet duplicate.");note.setTextSize(13);note.setTextColor(Color.DKGRAY);note.setPadding(0,0,0,dp(a,10));box.addView(note);
  List<DeviceInfo> list=new ArrayList<>(devices.values());Collections.sort(list,(x,y)->safe(x.name).compareToIgnoreCase(safe(y.name)));
  if(list.isEmpty()){TextView empty=new TextView(a);empty.setText("Tiada tablet berdaftar.");empty.setTextSize(16);box.addView(empty);}
  for(DeviceInfo d:list){LinearLayout row=new LinearLayout(a);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,dp(a,5),0,dp(a,5));LinearLayout info=new LinearLayout(a);info.setOrientation(LinearLayout.VERTICAL);TextView name=new TextView(a);name.setText(safe(d.name));name.setTextSize(17);name.setTextColor(Color.rgb(15,23,42));info.addView(name);TextView sub=new TextView(a);sub.setText((d.battery<0?"--":d.battery)+"% • Last update: "+last(d.lastSeen));sub.setTextSize(11);sub.setTextColor(Color.GRAY);info.addView(sub);row.addView(info,new LinearLayout.LayoutParams(0,-2,1));Button del=new Button(a);del.setText("PADAM");del.setTextSize(12);del.setAllCaps(false);del.setOnClickListener(v->confirm(a,d,cb));row.addView(del);box.addView(row);}
  new AlertDialog.Builder(a).setTitle("Urus Tablet Berdaftar").setView(scroll).setNegativeButton("Tutup",null).show();
 }
 private static void confirm(Activity a,DeviceInfo d,Callback cb){new AlertDialog.Builder(a).setTitle("Padam "+safe(d.name)+"?").setMessage("Ini hanya membuang rekod tablet ini daripada Monitor. Gunakan untuk rekod lama atau duplicate.\n\nTekan PADAM REKOD untuk sahkan.").setPositiveButton("PADAM REKOD",(x,w)->{Prefs.hideDevice(a,d.id);Prefs.removeKnownDevice(a,d.id);if(cb!=null)cb.onDeleted(d.id);Toast.makeText(a,"Rekod "+safe(d.name)+" dipadam",Toast.LENGTH_SHORT).show();}).setNegativeButton("Batal",null).show();}
 private static String safe(String s){return s==null||s.trim().isEmpty()?"Tablet":s;}private static String last(long t){if(t<=0)return"tiada rekod";return new SimpleDateFormat("dd/MM/yyyy h:mm a",Locale.getDefault()).format(new Date(t));}private static int dp(Context c,int v){return(int)(v*c.getResources().getDisplayMetrics().density+.5f);}
}
