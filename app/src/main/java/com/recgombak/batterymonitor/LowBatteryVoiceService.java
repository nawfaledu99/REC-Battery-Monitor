package com.recgombak.batterymonitor;

import android.app.*;
import android.content.*;
import android.media.*;
import android.os.*;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class LowBatteryVoiceService extends Service {
 private static final String CHANNEL="rec_low_battery_voice";
 private BroadcastReceiver receiver;
 private TextToSpeech tts;
 private boolean ttsReady=false, alerted=false;
 private final int threshold=65;

 @Override public void onCreate(){
  super.onCreate();
  createChannel();
  startForeground(4101,notification("Memantau bateri tablet"));
  if(!"tablet".equals(Prefs.getRole(this))){stopSelf();return;}
  tts=new TextToSpeech(getApplicationContext(),status->{
   if(status==TextToSpeech.SUCCESS&&tts!=null){
    ttsReady=true;
    try{
     tts.setLanguage(Locale.US);
     tts.setSpeechRate(0.92f);
     tts.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
    }catch(Exception ignored){}
    checkBattery();
   }
  });
  receiver=new BroadcastReceiver(){@Override public void onReceive(Context c,Intent i){checkBattery();}};
  IntentFilter f=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
  f.addAction(Intent.ACTION_POWER_CONNECTED);
  f.addAction(Intent.ACTION_POWER_DISCONNECTED);
  try{registerReceiver(receiver,f);}catch(Exception ignored){}
 }

 private void checkBattery(){
  Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
  if(i==null)return;
  int level=i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=i.getIntExtra(BatteryManager.EXTRA_SCALE,100);
  int pct=scale>0?Math.round(level*100f/scale):level;
  int status=i.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
  boolean charging=status==BatteryManager.BATTERY_STATUS_CHARGING||status==BatteryManager.BATTERY_STATUS_FULL;
  if(charging||pct>threshold){alerted=false;return;}
  if(pct>=0&&pct<=threshold&&!alerted&&ttsReady){
   alerted=true;
   try{
    AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
    am.setStreamVolume(AudioManager.STREAM_ALARM,am.getStreamMaxVolume(AudioManager.STREAM_ALARM),0);
    tts.speak("Tablet low battery. Please charge.",TextToSpeech.QUEUE_FLUSH,null,"low_battery");
    ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(4102,notification("Low battery "+pct+"% — please charge"));
   }catch(Exception ignored){}
  }
 }

 private void createChannel(){
  if(Build.VERSION.SDK_INT>=26){
   NotificationChannel c=new NotificationChannel(CHANNEL,"Tablet Low Battery Voice",NotificationManager.IMPORTANCE_LOW);
   getSystemService(NotificationManager.class).createNotificationChannel(c);
  }
 }
 private Notification notification(String text){
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);
  return b.setContentTitle("REC Battery Monitor").setContentText(text).setSmallIcon(android.R.drawable.ic_lock_idle_low_battery).setOngoing(true).build();
 }
 @Override public int onStartCommand(Intent i,int f,int id){return START_STICKY;}
 @Override public void onDestroy(){try{if(receiver!=null)unregisterReceiver(receiver);}catch(Exception ignored){}try{if(tts!=null){tts.stop();tts.shutdown();}}catch(Exception ignored){}super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}
