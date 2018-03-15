package com.example.dell.hotspotguard;

/**
 * Created by DELL on 3/1/2018.
 */
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.UUID;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context callingContext, Intent arg1) {


       DatabaseHelper myDb = new DatabaseHelper(callingContext.getApplicationContext());
        Cursor response = myDb.fetchSsid();


        WifiManager wm = (WifiManager) callingContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifi = new WifiConfiguration();
        String rand = UUID.randomUUID().toString().substring(0,8);
        while(response.moveToNext()){
            Log.d("ssid",response.getString(1));
            wifi.SSID = response.getString(1);
        }

        //Log.d("ssid",getUserSSID());
        wifi.preSharedKey = rand;
        wifi.hiddenSSID = true;
        wifi.status = WifiConfiguration.Status.ENABLED;
        wifi.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifi.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifi.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //wifi.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifi.allowedKeyManagement.set(4);
        wifi.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifi.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        try{
            Method setWifi = wm.getClass().getMethod("setWifiApEnabled",WifiConfiguration.class, boolean.class);
            if((Boolean) setWifi.invoke(wm,wifi,true)){
                Toast.makeText(callingContext,"Hotspot details set successfully",Toast.LENGTH_LONG).show();

                Log.d("running","now");
                setWifi.invoke(wm,wifi,false);
            }


        }
        catch (Exception e){
            Log.d(this.getClass().toString(),"error",e);
        }

    }

}
