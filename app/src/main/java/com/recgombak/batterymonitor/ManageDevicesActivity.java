package com.recgombak.batterymonitor;
import android.app.*;import android.os.*;import android.widget.*;import java.util.*;
public class ManageDevicesActivity extends Activity{
 @Override protected void onCreate(Bundle b){super.onCreate(b);if(!"monitor".equals(Prefs.getRole(this))){Toast.makeText(this,"Urus Tablet hanya untuk peranti Monitor",Toast.LENGTH_LONG).show();finish();return;}showManager();}
 private void showManager(){Map<String,DeviceInfo> m=Prefs.loadKnownDevices(this);for(String id:Prefs.getHidden(this))m.remove(id);DeviceManagerDialog.show(this,m,id->{m.remove(id);});}
}
