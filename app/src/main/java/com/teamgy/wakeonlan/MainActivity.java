package com.teamgy.wakeonlan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private MainFragment mainFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {


            @Override
            public void onBackStackChanged() {
                int stackHeight = getFragmentManager().getBackStackEntryCount();
                if (stackHeight > 1) {
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setHomeButtonEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setTitle("Wake On Lan");

                }
            }
        });

        mainFrag = new MainFragment();
        replaceFragmentContainer(mainFrag);



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
            startActivity(startSettings);

        }
        if(id == android.R.id.home){

            getFragmentManager().popBackStack();
            getSupportActionBar().setHomeAsUpIndicator(null);


        }

        return super.onOptionsItemSelected(item);
    }




    public void sendMagicPacket (View view) throws SocketException , IOException{

        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pref = sharedPreferences.getString("home_ssid",null);
        if(pref != null){

            Log.d("main", pref);

        }

        //TODO try catch here
        Intent serviceIntent = new Intent(getApplicationContext(),WOLService.class);
        String[] array = {macAdress};

        //TODO get from saved data

        serviceIntent.putExtra("macAdresses",array);

        startService(serviceIntent);*/
       // getFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditPCFragment()).commit();



    }
    public void startAddPcActivity(View view){

        /*Intent i = new Intent(this,EditPCFragment.class);
        startActivity(i);*/
        //we are adding a new pc, so no arguments for fragment
        EditPCFragment frag = EditPCFragment.newInstance(null);
        replaceFragmentContainer(frag);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check_white_24dp);
        getSupportActionBar().setTitle("Add new device");
        frag.addOnPCInfoAddedListener(new EditPCFragment.onPCInfoAddedListener() {
            @Override
            public void onPcInfoAdded(PCInfo pcInfo) {
                mainFrag.addNewPCInfo(pcInfo);
            }
        });



    }
    public void startEditPCFragment(View view,PCInfo pcInfo){

        EditPCFragment frag = EditPCFragment.newInstance(pcInfo);
        replaceFragmentContainer(frag);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check_white_24dp);


    }
    private void replaceFragmentContainer(Fragment newFragment){
        FragmentTransaction fts = getFragmentManager().beginTransaction();
        fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fts.replace(R.id.fragment_container, newFragment);
        fts.addToBackStack("tag");
        fts.commit();

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }

        else{
            finish();
            System.exit(0);
        }


    }
}

