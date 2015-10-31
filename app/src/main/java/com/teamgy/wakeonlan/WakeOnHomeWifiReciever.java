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

import java.sql.Array;

/**
 * Created by Jakov on 30/10/2015.
 */
public class WakeOnHomeWifiReciever  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){

                if(networkInfo.isConnected()){
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //i think this is needed because sometimes i get unknown ssid while its connecting to wifi
                    //then it sends the packet over that and something breaks.
                    if (wifiInfo.getSSID().equals("\"Thom_Nik\"") ) {

                        Intent serviceIntent = new Intent(context,WOLService.class);
                        //TODO get from saved data in broadcast reciever

                        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_key),Context.MODE_PRIVATE);
                        String mac = sharedPreferences.getString("macAdress",null);
                        if(mac != null){
                            String [] tmp = {mac};
                            serviceIntent.putExtra("macAdresses",tmp);

                            context.startService(serviceIntent);
                            Log.d("broadcast reciever:", "started service");
                        }
                    }
                    else{
                        Log.d("breciver", wifiInfo.getSSID() + "is not" + "Thom_Nik");
                    }

                }

            }

    }
}
