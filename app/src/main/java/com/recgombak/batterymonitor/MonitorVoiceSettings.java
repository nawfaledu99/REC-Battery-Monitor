package com.recgombak.batterymonitor;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.net.*;
import java.nio.charset.StandardCharsets;

public final class MonitorVoiceSettings {
 private final Activity a;
 private final EditText lowField, fullField;
 private final CheckBox lowRepeat, fullRepeat;
 private final Spinner lowEvery, fullEvery;
 private final LinearLayout view;
 private static final int[] MINS={1,3,5,10};

 public MonitorVoiceSettings(Activity activity, EditText low, EditText full){
  a=activity;lowField=low;fullField=full;
  view=new LinearLayout(a);view.setOrientation(LinearLayout.VERTICAL);
  TextView head=new TextView(a);head.setText("Amaran suara pada tablet");head.setTextSize(14);head.setTextColor(Color.DKGRAY);head.setPadding(0,12,0,4);view.addView(head);
  lowRepeat=new CheckBox(a);lowRepeat.setText("Bateri rendah: berulang sehingga dicas");lowRepeat.setChecked(Prefs.sp(a).getBoolean("monitor_voice_low_repeat",false));view.addView(lowRepeat);
  lowEvery=spinner(Prefs.sp(a).getInt("monitor_voice_low_min",3));view.addView(row("Ulang bateri rendah setiap",lowEvery));
  fullRepeat=new CheckBox(a);fullRepeat.setText("Cas penuh: berulang sehingga charger dicabut");fullRepeat.setChecked(Prefs.sp(a).getBoolean("monitor_voice_full_repeat",false));view.addView(fullRepeat);
  fullEvery=spinner(Prefs.sp(a).getInt("monitor_voice_full_min",3));view.addView(row("Ulang cas penuh setiap",fullEvery));
  lowRepeat.setOnCheckedChangeListener((b,c)->lowEvery.setEnabled(c));fullRepeat.setOnCheckedChangeListener((b,c)->fullEvery.setEnabled(c));lowEvery.setEnabled(lowRepeat.isChecked());fullEvery.setEnabled(fullRepeat.isChecked());
 }
 public View getView(){return view;}
 public void saveAndSend(boolean fullEnabled){int low=number(lowField,65),full=number(fullField,95),lowMin=selected(lowEvery),fullMin=selected(fullEvery);Prefs.sp(a).edit().putBoolean("monitor_voice_low_repeat",lowRepeat.isChecked()).putInt("monitor_voice_low_min",lowMin).putBoolean("monitor_voice_full_repeat",fullRepeat.isChecked()).putInt("monitor_voice_full_min",fullMin).apply();send(a,low,lowRepeat.isChecked(),lowMin,full,fullEnabled,fullRepeat.isChecked(),fullMin);}
 public static void sendCurrent(Activity a){send(a,Prefs.getThreshold(a),Prefs.sp(a).getBoolean("monitor_voice_low_repeat",false),Prefs.sp(a).getInt("monitor_voice_low_min",3),Prefs.getChargeThreshold(a),Prefs.chargeAlarmOnTablet(a),Prefs.sp(a).getBoolean("monitor_voice_full_repeat",false),Prefs.sp(a).getInt("monitor_voice_full_min",3));}
 private static void send(Activity a,int low,boolean lowRepeat,int lowMin,int full,boolean fullEnabled,boolean fullRepeat,int fullMin){new Thread(()->{try{JSONObject o=new JSONObject();o.put("app","REC_BATTERY_VOICE_1");o.put("low",low);o.put("lowRepeat",lowRepeat);o.put("lowMin",validMin(lowMin));o.put("full",full);o.put("fullEnabled",fullEnabled);o.put("fullRepeat",fullRepeat);o.put("fullMin",validMin(fullMin));byte[] d=o.toString().getBytes(StandardCharsets.UTF_8);try(DatagramSocket s=new DatagramSocket()){s.setBroadcast(true);DatagramPacket p=new DatagramPacket(d,d.length,InetAddress.getByName("255.255.255.255"),BatteryBroadcastService.PORT+2);for(int x=0;x<5;x++){s.send(p);Thread.sleep(150);}}}catch(Exception ignored){}}).start();}
 private Spinner spinner(int current){Spinner s=new Spinner(a);String[] labels={"1 minit","3 minit","5 minit","10 minit"};ArrayAdapter<String> ad=new ArrayAdapter<>(a,android.R.layout.simple_spinner_dropdown_item,labels);s.setAdapter(ad);int pos=1;for(int i=0;i<MINS.length;i++)if(MINS[i]==current)pos=i;s.setSelection(pos);return s;}
 private LinearLayout row(String text,Spinner s){LinearLayout r=new LinearLayout(a);r.setOrientation(LinearLayout.HORIZONTAL);TextView t=new TextView(a);t.setText(text);t.setTextSize(12);t.setTextColor(Color.DKGRAY);r.addView(t,new LinearLayout.LayoutParams(0,-2,1));r.addView(s,new LinearLayout.LayoutParams(-2,-2));return r;}
 private int selected(Spinner s){int p=s.getSelectedItemPosition();return p>=0&&p<MINS.length?MINS[p]:3;}private int number(EditText e,int def){try{return Math.max(1,Math.min(100,Integer.parseInt(e.getText().toString().trim())));}catch(Exception x){return def;}}private static int validMin(int v){return v==1||v==3||v==5||v==10?v:3;}
}
