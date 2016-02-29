package com.teamgy.wakeonlan.widget;

import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.gui.PCInfoAdapterCheckboxes;
import com.teamgy.wakeonlan.gui.PCListHolderFragment;
import com.teamgy.wakeonlan.utils.PCInfoDatabaseHelper;
import com.teamgy.wakeonlan.utils.Tools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jedi-Windows on 21.02.2016..
 */
public class ConfigureWidgetActivity extends AppCompatActivity {
    private int widgetID;
    private PCListHolderFragment pcListHolderFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED); //default result if ok was not pressed

        setContentView(R.layout.configure_widget);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Select PCs for widget");


        PCInfoDatabaseHelper db  =PCInfoDatabaseHelper.getsInstance(getBaseContext());
        ArrayList<PCInfo> pcInfos = db.getAllPCInfos();

         this.pcListHolderFragment = PCListHolderFragment.newInstance(
                new PCInfoAdapterCheckboxes(getBaseContext(),pcInfos)
        );

        FragmentTransaction fts = getFragmentManager().beginTransaction();
        fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fts.replace(R.id.configure_widget_fragment_contianer, pcListHolderFragment);
        fts.commit();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_pc_activity, menu);
        //its ok to reuse this, it only has a checkmark thing
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("edit", "called it");
        if (item.getItemId() == R.id.menu_pc_check) {
            ArrayList<PCInfo> selectedPCInfos = getSelectedPCInfos();
            finishConfiguration(selectedPCInfos);
            return true;
        }else{
            finish();
        }
        return true;
    }
    private ArrayList<PCInfo> getSelectedPCInfos(){
        //gests all selected pc infos from listview

        ListView listView = pcListHolderFragment.getListview();
        ArrayList<PCInfo> pcInfos = new ArrayList<>();

        for(int i = 0; i < listView.getChildCount(); i++){
            View v = listView.getChildAt(i);
            Checkable checkable = (Checkable) v.findViewById(R.id.pc_list_item_checkbox);
            if(checkable.isChecked()){
                pcInfos.add(pcListHolderFragment.getPCInfo(i));
            }
        }
        return pcInfos;
    }
    private void finishConfiguration(ArrayList<PCInfo> selectedPCs){
        final Context context = ConfigureWidgetActivity.this;

        saveWidgetData(selectedPCs);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
       // appWidgetManager.updateAppWidget(widgetID,views);
        //WidgetProvider.updateAppWidget(context,appWidgetManager,widgetID);
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, WidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {widgetID});
        sendBroadcast(intent);//this will call widget provider onUpdate method.

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_OK, resultValue);
        finish();

    }

    private void saveWidgetData(ArrayList<PCInfo> selectedPCs){
        //each widget has his pcInfos
        //widget provider retrieves this as {widgetId:[pcinfo1,pcinfo2...]}
        //saves widgets pcinfos into shared prefs
        SharedPreferences prefs = getSharedPreferences(getString(R.string.widget_preference_file_key),0);

        HashMap<Integer,ArrayList<PCInfo>> newWidgetData = new HashMap<Integer,ArrayList<PCInfo>>();
        newWidgetData.put(widgetID,selectedPCs);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Type type = new TypeToken<HashMap<Integer, ArrayList<PCInfo>>>(){}.getType();


        String loadedWidgetdataRaw = prefs.getString(getString(R.string.saved_widget_data), "");
        if(loadedWidgetdataRaw != ""){
            //this is not the first time saving
            //retrieve the data and append to it]
            HashMap<Integer,ArrayList<PCInfo>> oldWidgetData = gson.fromJson(loadedWidgetdataRaw,type);
            newWidgetData.putAll(oldWidgetData);
        }
        //finally save the hashmap as json object
        prefs.edit().putString(getString(R.string.saved_widget_data),gson.toJson(newWidgetData)).commit();
    }
}


