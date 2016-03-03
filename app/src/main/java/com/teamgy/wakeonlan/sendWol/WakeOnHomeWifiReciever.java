package com.teamgy.wakeonlan.sendWol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.utils.PCInfoDatabaseHelper;
import com.teamgy.wakeonlan.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jakov on 30/10/2015.
 */
public class WakeOnHomeWifiReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isQuietHoursActive(context)){
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnected()) {
                    //since this method always gets called twice
                    //i added so that it can only get called once in 5 seconds
                    SharedPreferences prefs = context.getSharedPreferences("wifi_receiver",0);
                    long lastTimeConnected = prefs.getLong("lastTimeConnected",0);
                    Date now = new Date();
                    if(lastTimeConnected + 5 < now.getTime() /1000 || lastTimeConnected == 0 ){
                        //if last receive was more than 5 seconds ago
                        prefs.edit().putLong("lastTimeConnected",now.getTime()/1000).commit();

                    }else{
                        //last time this method was called was less than 5 seconds ago
                        // silently ignore it..
                        Log.d("wolreciever", "onReceive called less than 5 seconds ago");
                        return;
                    }
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    PCInfoDatabaseHelper dbHelper = PCInfoDatabaseHelper.getsInstance(context);
                    ArrayList<PCInfo> pcInfos = dbHelper.getAllPCInfos();
                    ArrayList<PCInfo> pcInfosToSend = new ArrayList<>();

                    for (PCInfo pcInfo : pcInfos) {
                        if (!wifiInfo.getSSID().equals("<unknown ssid>")) {

                            Log.d("wolreciever", wifiInfo.getSSID() + "is " + "\"" + pcInfo.getPcName() + "\"");
                            pcInfosToSend.add(pcInfo);
                        } else {
                            Log.d("wolreciever", "we got unknow ssid" + wifiInfo.getSSID());
                        }
                    }
                    if (pcInfosToSend.size() > 0) {
                        prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        int wolPort = Integer.parseInt(prefs.getString(context.getString(R.string.key_pref_wol_port),"40000"));
                        int wolRetry = Integer.parseInt(prefs.getString(context.getString(R.string.key_pref_retry_amount),"3"));

                        Intent serviceIntent = new Intent(context, WOLService.class);
                        serviceIntent.putStringArrayListExtra("macAdresses", Tools.pcInfosToMacArrayList(pcInfosToSend));
                        serviceIntent.putExtra("retryInteval",wolRetry);
                        serviceIntent.putExtra("wolPort",wolPort);

                        context.startService(serviceIntent);

                        Log.d("wolreciever", "started service");

                    }
                }
            }
        }else{
            Log.d("wolreciever", "quiet hours active, not waking up.");
        }
    }


    private boolean isQuietHoursActive(Context c){
        String keyQuietWolEnabled = c.getString(R.string.key_quiet_hours_enabled);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean isQuietHoursEnabled = prefs.getBoolean(keyQuietWolEnabled,false);

        if(isQuietHoursEnabled){
           // int [] hourTimeStart = prefs.getInt(c.getString(R.string.key_time_start),-1);
            int [] timeStart  = Tools.loadJsonTime(c, c.getString(R.string.key_time_start));
            int [] timeEnd = Tools.loadJsonTime(c,c.getString(R.string.key_time_end));

            boolean timeRangeSet = timeStart != null && timeEnd != null;
            Log.d("wolreciever", "time range is not set, allowing wake up");
            if(timeRangeSet){
                try{
                    SimpleDateFormat _24hourFormat = new SimpleDateFormat("HH:mm");
                    Date dateTimeStart = _24hourFormat.parse(timeStart[0] + ":" + timeStart[1]);
                    Date dateTimeEnd = _24hourFormat.parse(timeEnd[0] + ":" + timeEnd[1]);
                    Date dateNow = new Date();
                    Log.d("wolreciever", "datenow : " + dateNow.toString());
                    return isTimeInRange(dateTimeStart, dateTimeEnd, dateNow);

                }catch (ParseException e){
                    e.printStackTrace();
                    return true;
                }
            }
        }
        Log.d("wolreciever", "queit hours not enabled, allowed waking up");

        return false;
    }

    public boolean isTimeInRange(Date timeStart, Date timeEnd, Date timequestioned) {
        //seconds are a lot easier for comparing time ranges
        int timeStartSeconds = timeStart.getHours() * 3600 + timeStart.getMinutes() * 60;

        int timeEndSeconds = timeEnd.getHours() * 3600 + timeEnd.getMinutes() * 60;

        int currentSeconds =  timequestioned.getHours() * 3600 + timequestioned.getMinutes() *  60;
        boolean isInTimeRange = false;
        if(timeStartSeconds > timeEndSeconds){
            isInTimeRange = currentSeconds <= timeStartSeconds &&  currentSeconds <= timeEndSeconds;

        }
        else{
            isInTimeRange = currentSeconds >= timeStartSeconds && currentSeconds <= timeEndSeconds;

        }
        Log.d("wolreciever", "in time range? " + isInTimeRange);
        Log.d("wolreciever", "timeStartSecs: " + timeStartSeconds + "timeEndSeconds: " + timeEndSeconds +
              "currentSeconds: " + currentSeconds);
        return isInTimeRange;
    }
}
