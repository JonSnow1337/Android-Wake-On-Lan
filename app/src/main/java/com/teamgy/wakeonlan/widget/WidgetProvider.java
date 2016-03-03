package com.teamgy.wakeonlan.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.sendWol.WOLService;
import com.teamgy.wakeonlan.utils.Tools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WidgetProvider extends AppWidgetProvider {

    private static final String BUTTON_CLICKED    = "buttonClicked";
    private static String stringToLog = "default string";

    private  HashMap<Integer,ArrayList<PCInfo>> widgetData =null;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i = 0; i < appWidgetIds.length; i++){

            widgetData = loadWidgetData(context); //this might not be needed..
            RemoteViews remoteViews;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(BUTTON_CLICKED);
            intent.putExtra("widgetID", appWidgetIds[i]);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, appWidgetIds[i], intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        if (BUTTON_CLICKED.equals(intent.getAction())) {
            if(widgetData == null){
                widgetData = loadWidgetData(context);
                //it might be null even after this
            }
            //put this in a method
            int widgetID = intent.getExtras().getInt("widgetID");
            if(widgetData != null){
                ArrayList<PCInfo> pcInfos = widgetData.get(Integer.valueOf(widgetID));
                Intent serviceIntent = new Intent(context, WOLService.class);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                int wolPort = Integer.parseInt(prefs.getString(context.getString(R.string.key_pref_wol_port),"40000"));

                serviceIntent.putStringArrayListExtra("macAdresses", Tools.pcInfosToMacArrayList(pcInfos));
                serviceIntent.putExtra("retryInteval",1);
                serviceIntent.putExtra("retrySleep",0);
                serviceIntent.putExtra("wolPort",wolPort);



                context.startService(serviceIntent);
            }
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    private HashMap<Integer,ArrayList<PCInfo>>  loadWidgetData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.widget_preference_file_key),0);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String loadedWidgetdataRaw = prefs.getString(context.getString(R.string.saved_widget_data), "");
        HashMap<Integer,ArrayList<PCInfo>> result = null;
        if(loadedWidgetdataRaw != ""){
            Type type = new TypeToken<HashMap<Integer, ArrayList<PCInfo>>>(){}.getType();
            result = gson.fromJson(loadedWidgetdataRaw,type);
        }
        else{
            return null;
        }
        return result;
    }
}