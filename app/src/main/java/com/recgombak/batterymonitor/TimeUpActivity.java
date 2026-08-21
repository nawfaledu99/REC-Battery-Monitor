package com.recgombak.batterymonitor;

import android.app.*;import android.content.*;import android.os.*;import android.graphics.Color;import android.view.*;import android.widget.*;

public class TimeUpActivity extends Activity {
 @Override protected void onCreate(Bundle b){super.onCreate(b);if(Build.VERSION.SDK_INT>=27){setShowWhenLocked(true);setTurnScreenOn(true);}getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);build();}
 private void build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(24),dp(24),dp(24),dp(24));root.setBackgroundColor(Color.rgb(180,0,0));root.addView(tv("⛔",72,true));root.addView(tv("MASA TAMAT",34,true));TextView sub=tv("Masa penggunaan tablet telah tamat.",20,false);sub.setPadding(0,dp(12),0,dp(24));root.addView(sub);Button ok=new Button(this);ok.setText("OK");ok.setTextSize(18);ok.setOnClickListener(v->dismissAndClose());root.addView(ok,new LinearLayout.LayoutParams(-1,dp(56)));setContentView(root);}
 private void dismissAndClose(){((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(2002);Intent i=new Intent(this,BatteryBroadcastService.class);i.setAction("ACK_LOCAL");try{if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);}catch(Exception ignored){}finish();}
 private TextView tv(String s,int sp,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextColor(Color.WHITE);v.setTextSize(sp);v.setGravity(Gravity.CENTER);if(bold)v.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return v;}
 private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
}
