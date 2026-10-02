package com.recgombak.batterymonitor;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
 private LinearLayout root;

 @Override protected void onCreate(Bundle b){
  super.onCreate(b);
  if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
   requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},44);
  Prefs.setRole(this,"tablet");
  buildUi();
  if(Prefs.isEnabled(this))startVoiceService();
 }

 private void buildUi(){
  ScrollView s=new ScrollView(this);
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(12),dp(12),dp(12),dp(18));root.setBackgroundColor(Color.rgb(247,248,250));s.addView(root);
  root.addView(tv("REC Battery Monitor",25,Color.rgb(15,23,42),true));
  root.addView(tv("Mod TABLET",12,Color.DKGRAY,true));
  buildTablet();
  TextView f=tv("Version 3.2 LITE • Developed by cigunofal",11,Color.GRAY,true);f.setGravity(Gravity.CENTER);root.addView(f);
  setContentView(s);
 }

 private void buildTablet(){
  LinearLayout c=card();c.addView(tv("TABLET INI",11,Color.GRAY,true));
  TextView name=tv(Prefs.getName(this),22,Color.rgb(15,23,42),true);c.addView(name);
  Button rename=smallButton("Tukar nama");rename.setOnClickListener(v->rename(name));c.addView(rename);
  c.addView(tv("Pemantauan bateri aktif",15,Color.rgb(22,120,60),true));
  TextView current=tv("LOW ≤ "+Prefs.sp(this).getInt("voice_low_threshold",Prefs.getThreshold(this))+"%   •   FULL ≥ "+Prefs.sp(this).getInt("voice_full_threshold",Prefs.getChargeThreshold(this))+"%",18,Color.rgb(15,23,42),true);current.setPadding(0,dp(10),0,dp(6));c.addView(current);
  Button settings=button("⚙ Tetapan Bateri Tablet Ini");settings.setOnClickListener(v->localBatterySettings(current));c.addView(settings);root.addView(c);
  Button bg=button("Benarkan berjalan di background");bg.setOnClickListener(v->openBatterySettings());root.addView(bg);
 }

 private void localBatterySettings(TextView current){
  LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);b.setPadding(dp(8),dp(4),dp(8),0);
  EditText low=num(""+Prefs.sp(this).getInt("voice_low_threshold",Prefs.getThreshold(this)));
  EditText full=num(""+Prefs.sp(this).getInt("voice_full_threshold",Prefs.getChargeThreshold(this)));
  b.addView(label("LOW — beri amaran pada / bawah (%)"));b.addView(low);
  b.addView(label("FULL — beri amaran ketika dicas pada / atas (%)"));b.addView(full);
  CheckBox lr=new CheckBox(this);lr.setText("Ulang suara LOW sehingga dicas");lr.setChecked(Prefs.sp(this).getBoolean("voice_low_repeat",false));b.addView(lr);
  Spinner lm=repeatSpinner(Prefs.sp(this).getInt("voice_low_repeat_min",3));b.addView(label("Ulang LOW setiap"));b.addView(lm);
  CheckBox fr=new CheckBox(this);fr.setText("Ulang suara FULL sehingga charger dicabut");fr.setChecked(Prefs.sp(this).getBoolean("voice_full_repeat",false));b.addView(fr);
  Spinner fm=repeatSpinner(Prefs.sp(this).getInt("voice_full_repeat_min",3));b.addView(label("Ulang FULL setiap"));b.addView(fm);
  lm.setEnabled(lr.isChecked());fm.setEnabled(fr.isChecked());lr.setOnCheckedChangeListener((v,x)->lm.setEnabled(x));fr.setOnCheckedChangeListener((v,x)->fm.setEnabled(x));
  new AlertDialog.Builder(this).setTitle("Tetapan Bateri Tablet").setView(b).setPositiveButton("Simpan",(d,w)->{
   int lo=Prefs.getThreshold(this),fu=Prefs.getChargeThreshold(this);
   try{lo=Math.max(1,Math.min(100,Integer.parseInt(low.getText().toString())));fu=Math.max(1,Math.min(100,Integer.parseInt(full.getText().toString())));}catch(Exception ignored){}
   Prefs.setThreshold(this,lo);Prefs.setChargeThreshold(this,fu);
   Prefs.sp(this).edit().putInt("voice_low_threshold",lo).putBoolean("voice_low_repeat",lr.isChecked()).putInt("voice_low_repeat_min",repeatMinutes(lm)).putInt("voice_full_threshold",fu).putBoolean("voice_full_enabled",true).putBoolean("voice_full_repeat",fr.isChecked()).putInt("voice_full_repeat_min",repeatMinutes(fm)).putLong("voice_last_low",0).putLong("voice_last_full",0).apply();
   current.setText("LOW ≤ "+lo+"%   •   FULL ≥ "+fu+"%");startVoiceService();Toast.makeText(this,"Tetapan disimpan dalam tablet ini",Toast.LENGTH_LONG).show();
  }).setNegativeButton("Batal",null).show();
 }

 private void startVoiceService(){Intent v=new Intent(this,LowBatteryVoiceService.class);try{if(Build.VERSION.SDK_INT>=26)startForegroundService(v);else startService(v);}catch(Exception ignored){}}
 private Spinner repeatSpinner(int selected){Spinner s=new Spinner(this);String[] labels={"1 minit","3 minit","5 minit","10 minit"};s.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,labels));int[] vals={1,3,5,10};for(int i=0;i<vals.length;i++)if(vals[i]==selected)s.setSelection(i);return s;}
 private int repeatMinutes(Spinner s){int[] vals={1,3,5,10};int i=s.getSelectedItemPosition();return i>=0&&i<vals.length?vals[i]:3;}
 private void openBatterySettings(){try{startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));}catch(Exception ignored){}}
 private void rename(TextView v){EditText e=new EditText(this);e.setText(Prefs.getName(this));new AlertDialog.Builder(this).setTitle("Nama tablet").setView(e).setPositiveButton("Simpan",(d,w)->{String n=e.getText().toString().trim();if(!n.isEmpty()){Prefs.setName(this,n);v.setText(n);}}).setNegativeButton("Batal",null).show();}
 private EditText num(String s){EditText e=new EditText(this);e.setInputType(2);e.setText(s);return e;}
 private TextView label(String s){return tv(s,12,Color.DKGRAY,true);}
 private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(11),dp(8),dp(11),dp(8));android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();bg.setColor(Color.WHITE);bg.setCornerRadius(dp(12));bg.setStroke(dp(1),Color.LTGRAY);c.setBackground(bg);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(4),0,dp(3));c.setLayoutParams(lp);return c;}
 private TextView tv(String t,int s,int col,boolean bold){TextView v=new TextView(this);v.setText(t);v.setTextSize(s);v.setTextColor(col);if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
 private Button button(String t){Button b=new Button(this);b.setText(t);b.setAllCaps(false);return b;}
 private Button smallButton(String t){Button b=button(t);b.setTextSize(10);b.setMinHeight(0);b.setMinimumHeight(0);b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(dp(9),dp(2),dp(9),dp(2));return b;}
 private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
}
