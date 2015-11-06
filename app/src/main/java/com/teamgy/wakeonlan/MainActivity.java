package com.teamgy.wakeonlan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  OnCreateViewListener{

    private MainFragment mainFrag;
    final static int REQUEST_EDIT = 1;
    final static int REQUEST_ADD=2;

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





        if(savedInstanceState == null){

            mainFrag = new MainFragment();
            mainFrag.setOnCreateViewListener(this); //to set click listview listener
            replaceFragmentContainer(mainFrag, false);

        }
        else{
            mainFrag = (MainFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
            mainFrag.setOnCreateViewListener(this);
            replaceFragmentContainer(mainFrag, false);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        if(id == R.id.action_launch_reqs){


            try {
                sendMagicPacket(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }




    public void sendMagicPacket (View view) throws SocketException , IOException{

        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pref = sharedPreferences.getString("home_ssid",null);
        if(pref != null){

            Log.d("main", pref);

        }*/

        //TODO try catch here
        Intent serviceIntent = new Intent(getApplicationContext(),WOLService.class);
        ArrayList<PCInfo> pcInfos = mainFrag.getPcinfoArrList();
        ArrayList<String> macArraylist = pcInfosToMacArrayList(pcInfos);


        //TODO get from saved data
        String[] arr = macArraylist.toArray(new String[macArraylist.size()]);
        serviceIntent.putExtra("macAdresses", arr);
        startService(serviceIntent);
       // getFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditPCActivity()).commit();



    }
   public void startAddPcFragment(View view){

       Intent i = new Intent(this,EditPCActivity.class);
       i.putExtra("mode", REQUEST_ADD);
       startActivityForResult(i, REQUEST_ADD);

    }

    public void startEditPCFragment(View view,PCInfo pcInfo, final int position){
    /*
        EditPCActivity frag = EditPCActivity.newInstance(pcInfo);
        replaceFragmentContainer(frag,true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check_white_24dp);
        getSupportActionBar().setTitle("Add new device");
        frag.addOnPCInfoAddedListener(new EditPCActivity.onPCInfoAddedListener() {
            @Override
            public void onPcInfoAdded(PCInfo pcInfo, boolean edited) {
                if (edited) {
                    mainFrag.editPCInfo(pcInfo, position);
                } else {
                    mainFrag.addNewPCInfo(pcInfo);
                }
            }
        });*/

        Intent i = new Intent(this,EditPCActivity.class);
        i.putExtra("mode", REQUEST_EDIT);
        if(pcInfo != null){

            i.putExtra("macAdress", pcInfo.getMacAdress());
            i.putExtra("ssid", pcInfo.getSSID());
            i.putExtra("position",position);

        }
        startActivityForResult(i, REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("main", "recieved result");
        PCInfo result = new PCInfo(data.getStringExtra("macAdress"),data.getStringExtra("ssid"));
        if(requestCode == REQUEST_EDIT){

            int pos = data.getIntExtra("position",0);
            mainFrag.editPCInfo(result,pos);



        }
        if(requestCode == REQUEST_ADD){

            mainFrag.addNewPCInfo(result);


        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void replaceFragmentContainer(Fragment newFragment,boolean addToBack){
        FragmentTransaction fts = getFragmentManager().beginTransaction();
        fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fts.replace(R.id.fragment_container, newFragment);
        if(addToBack){
            fts.addToBackStack(null);
        }
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
    public static ArrayList<String> pcInfosToMacArrayList(ArrayList<PCInfo> pcInfos){
        ArrayList<String> macArraylist = new ArrayList<String>();
        for (PCInfo pcInfo : pcInfos) {
            macArraylist.add(pcInfo.getMacAdress());

        }
        return macArraylist;



    }

    @Override
    public void onViewCreated() {
        ListView listView = mainFrag.getListview();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.pc_item_checkbox);
                Tools.changeCheckboxState(checkBox);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO maybe customize items to contain pcInfo somehow?
                TextView tvMac = (TextView) view.findViewById(R.id.list_item_mac);
                TextView tvSSID = (TextView) view.findViewById(R.id.list_item_ssid);
                PCInfo info = new PCInfo(tvMac.getText().toString(), tvSSID.getText().toString());
                // startEditPCFragment(view,);
                startEditPCFragment(view, info, position);
                return true;
            }
        });

    }
}

