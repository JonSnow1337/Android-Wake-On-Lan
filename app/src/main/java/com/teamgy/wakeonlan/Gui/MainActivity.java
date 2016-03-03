package com.teamgy.wakeonlan.gui;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teamgy.wakeonlan.data.PCInfo;
import com.teamgy.wakeonlan.R;
import com.teamgy.wakeonlan.gui.settings.SettingsActivity;
import com.teamgy.wakeonlan.gui.appIntro.MyIntro;
import com.teamgy.wakeonlan.sendWol.WOLService;
import com.teamgy.wakeonlan.utils.AndroidDatabaseManager;
import com.teamgy.wakeonlan.utils.PCInfoDatabaseHelper;
import com.teamgy.wakeonlan.utils.Tools;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnCreateViewListener {

    public static final int RESULT_DELETE = 10;
    private PCListHolderFragment mainFrag;
    final static int REQUEST_EDIT = 1;
    final static int REQUEST_ADD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseIntro();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {


            @Override
            public void onBackStackChanged() {
                int stackHeight = getFragmentManager().getBackStackEntryCount();
                if (stackHeight > 0) {
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setHomeButtonEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setTitle("Wake On Lan");

                }
            }
        });


        if (savedInstanceState == null) {
            mainFrag = new PCListHolderFragment();
        } else {
            mainFrag = (PCListHolderFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        }
        mainFrag.setOnCreateViewListener(this); //to set click listview listener
        replaceFragmentContainer(mainFrag, false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent startSettings = new Intent(this, SettingsActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(startSettings, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                startActivity(startSettings);
            }

        }
        if (id == android.R.id.home) {

            getFragmentManager().popBackStack();
            getSupportActionBar().setHomeAsUpIndicator(null);
        }
        if (id == R.id.action_launch_reqs) {
                sendMagicPacket(null);
        }
        if (id == R.id.debug_database) {

            Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbmanager);

        }
        if(id == R.id.action_share_app){

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_playstore_link));
            startActivity(Intent.createChooser(share, "Share App Link"));
        }
        return super.onOptionsItemSelected(item);
    }


    public void sendMagicPacket(View view) {

        Intent serviceIntent = new Intent(getApplicationContext(), WOLService.class);
        ArrayList<PCInfo> pcInfos = mainFrag.getPcinfoArrList();
        ArrayList<String> enabledMacs = Tools.getEnabledMacs(pcInfos);
        serviceIntent.putStringArrayListExtra("macAdresses", enabledMacs);
        startService(serviceIntent);
        if(pcInfos.size() == 0){
            Snackbar.make(findViewById(R.id.fab), "Add a PC to the list first!", Snackbar.LENGTH_SHORT).show();
        }else if (enabledMacs.size() == 0){
            Snackbar.make(findViewById(R.id.fab), "Enable a PC from the list!", Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(findViewById(R.id.fab), "Requests sent! Your PC should turn on. ", Snackbar.LENGTH_SHORT).show();
        }

    }

    public void startAddPcFragment(View view) {

        Intent i = new Intent(this, EditPCActivity.class);
        i.putExtra("mode", REQUEST_ADD);

        View sharedView = findViewById(R.id.fab);
        String transitionName = getString(R.string.transition);

        ActivityOptions transitionActivityOptions = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
            startActivityForResult(i, REQUEST_ADD, transitionActivityOptions.toBundle());
        } else {
            startActivityForResult(i, REQUEST_ADD);
        }

    }

    public void startEditPCActivity(View view, PCInfo pcInfo, final int position) {


        Intent i = new Intent(this, EditPCActivity.class);
        i.putExtra("mode", REQUEST_EDIT);
        if (pcInfo != null) {
            i.putExtra("pcInfo", pcInfo);
            i.putExtra("position", position);
        }

        startActivityForResult(i, REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("main", "recieved result");

        PCInfoDatabaseHelper dbHelper = PCInfoDatabaseHelper.getsInstance(this);
        if (resultCode != RESULT_CANCELED) {

            PCInfo result = (PCInfo) data.getSerializableExtra("pcInfo");
            if (requestCode == REQUEST_EDIT) {

                if (resultCode == RESULT_DELETE) {
                    int pos = data.getIntExtra("position", 0);
                    mainFrag.deletePcInfo(pos);


                }
                if (resultCode == RESULT_OK) {

                    int pos = data.getIntExtra("position", 0);
                    mainFrag.editPCInfo(result, pos);
                }

            }
            if (requestCode == REQUEST_ADD) {
                //if we are adding new item set it to enabled by default
                result.setOnWifiEnabled(true);
                mainFrag.addNewPCInfo(result);
                dbHelper.addPCInfo(result);


            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void replaceFragmentContainer(Fragment newFragment, boolean addToBack) {
        FragmentTransaction fts = getFragmentManager().beginTransaction();
        fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fts.replace(R.id.fragment_container, newFragment);
        if (addToBack) {
            fts.addToBackStack(null);
        }
        fts.commit();

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            finish();
            System.exit(0);
        }
    }








    @Override
    public void onViewCreated() {
        ListView listView = mainFrag.getListview();
        //holding the listview thingy
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    PCInfo info = mainFrag.getPCInfo(position);
                    startEditPCActivity(view, info, position);
                    return true;
                }
            });

        }
        //clickign the edit button
        PcInfoAdapter adapter = (PcInfoAdapter) listView.getAdapter();
        adapter.setCallback(new PcInfoAdapter.PCInfoAdapterCallback() {
            @Override
            public void configurePressed(PCInfo pcinfo, int position) {
                startEditPCActivity(null,pcinfo,position);

            }
        });

    }
    public PCListHolderFragment getMainFrag() {
        return mainFrag;
    }

    public void initialiseIntro(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();


    }
    public void clickTest(View v){
        Log.d("test","clicked");
    }

    public void networkDiscovery(View v){
    }
}



