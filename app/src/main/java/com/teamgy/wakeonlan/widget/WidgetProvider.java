package com.teamgy.wakeonlan.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";
    private static String stringToLog = "default string";

    private static HashMap<Integer,ArrayList<PCInfo>> widgetData = new HashMap<Integer,ArrayList<PCInfo>> ();
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i = 0; i < appWidgetIds.length; i++){
            RemoteViews remoteViews;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(SYNC_CLICKED);
            intent.putExtra("widgetID", appWidgetIds[i]);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        if (SYNC_CLICKED.equals(intent.getAction())) {
            //put this in a method
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.widget_preference_file_key),0);
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String loadedWidgetdataRaw = prefs.getString(context.getString(R.string.saved_widget_data), "");
            Type type = new TypeToken<HashMap<Integer, ArrayList<PCInfo>>>(){}.getType();
            widgetData = gson.fromJson(loadedWidgetdataRaw,type);
            int widgetID = intent.getExtras().getInt("widgetID");
            ArrayList<PCInfo> pcInfos = widgetData.get(Integer.valueOf(widgetID));
            Intent serviceIntent = new Intent(context, WOLService.class);
            serviceIntent.putStringArrayListExtra("macAdresses", Tools.pcInfosToMacArrayList(pcInfos));
            context.startService(serviceIntent);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId){
        //i have no idea why i have to do it like this..
        //no idea whats the point of the onUpdate method either.

        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(SYNC_CLICKED);
        intent.putExtra("widgetID", appWidgetId);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, appWidgetId, intent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_button,pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    }

}