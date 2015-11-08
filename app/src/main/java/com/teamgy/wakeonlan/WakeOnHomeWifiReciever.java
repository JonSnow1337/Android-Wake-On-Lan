package com.teamgy.wakeonlan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.teamgy.wakeonlan.SQL.PCInfoDatabaseHelper;

import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by Jakov on 30/10/2015.
 */
public class WakeOnHomeWifiReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            if (networkInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                PCInfoDatabaseHelper dbHelper = PCInfoDatabaseHelper.getsInstance(context);
                ArrayList<PCInfo> pcInfos = dbHelper.getAllPCInfos();
                ArrayList<PCInfo> pcInfosToSend = new ArrayList<>();

                for (PCInfo pcInfo : pcInfos) {

                    if (wifiInfo.getSSID().equals("\"" + pcInfo.getSSID() + "\"")) {

                        Log.d("breciver", wifiInfo.getSSID() + "is " + "\"" + pcInfo.getSSID() + "\"");
                        pcInfosToSend.add(pcInfo);

                    } else {

                        Log.d("breciver", wifiInfo.getSSID() + "is not" + "\"" + pcInfo.getSSID() + "\"");
                    }

                }

                Intent serviceIntent = new Intent(context, WOLService.class);

                serviceIntent.putStringArrayListExtra("macAdresses", Tools.pcInfosToMacArrayList(pcInfosToSend));

                context.startService(serviceIntent);
                Log.d("broadcast reciever:", "started service");


            }

        }

    }
}
