package com.teamgy.wakeonlan;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.teamgy.wakeonlan.utils.AndroidDatabaseManager;
import com.teamgy.wakeonlan.utils.PCInfoDatabaseHelper;
import com.teamgy.wakeonlan.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnCreateViewListener {

    public static final int RESULT_DELETE = 10;
    private MainFragment mainFrag;
    final static int REQUEST_EDIT = 1;
    final static int REQUEST_ADD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            mainFrag = new MainFragment();
            mainFrag.setOnCreateViewListener(this); //to set click listview listener
            replaceFragmentContainer(mainFrag, false);

        } else {
            mainFrag = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            mainFrag.setOnCreateViewListener(this);
            replaceFragmentContainer(mainFrag, false);

        }

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
            }

        }
        if (id == android.R.id.home) {

            getFragmentManager().popBackStack();
            getSupportActionBar().setHomeAsUpIndicator(null);
        }
        if (id == R.id.action_launch_reqs) {


            try {
                sendMagicPacket(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (id == R.id.debug_database) {

            Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbmanager);

        }


        return super.onOptionsItemSelected(item);
    }


    public void sendMagicPacket(View view) throws IOException {

        Intent serviceIntent = new Intent(getApplicationContext(), WOLService.class);
        ArrayList<PCInfo> pcInfos = mainFrag.getPcinfoArrList();
        ArrayList<String> enabledMacs = Tools.getEnabledMacs(pcInfos);
        serviceIntent.putStringArrayListExtra("macAdresses", enabledMacs);
        startService(serviceIntent);
        Snackbar.make(findViewById(R.id.fab), "Requests sent!", Snackbar.LENGTH_SHORT).show();
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

            i.putExtra("macAdress", pcInfo.getMacAdress());
            i.putExtra("ssid", pcInfo.getPcName());
            i.putExtra("position", position);

        }

        startActivityForResult(i, REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("main", "recieved result");

        PCInfoDatabaseHelper dbHelper = PCInfoDatabaseHelper.getsInstance(this);
        if (resultCode != RESULT_CANCELED) {

            PCInfo result = new PCInfo(data.getStringExtra("macAdress"), data.getStringExtra("ssid"));
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
                result.setEnabled(true);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.pc_item_checkbox);
                Tools.changeCheckboxState(checkBox);
                PCInfo pcToEdit = mainFrag.getPCInfo(position);
                mainFrag.editPCInfo(new PCInfo(pcToEdit.getMacAdress(), pcToEdit.getPcName(), checkBox.isChecked()), position); //just chaning enabled state of pcinfo
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO maybe customize items to contain pcInfo somehow?
                TextView tvMac = (TextView) view.findViewById(R.id.list_item_mac);
                TextView tvSSID = (TextView) view.findViewById(R.id.list_item_ssid);
                PCInfo info = new PCInfo(tvMac.getText().toString(), tvSSID.getText().toString());
                startEditPCActivity(view, info, position);
                return true;
            }
        });


    }

}



