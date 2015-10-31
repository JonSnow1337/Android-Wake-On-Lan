package com.teamgy.wakeonlan;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private String macAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.edit_text);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE);

        macAdress = sharedPreferences.getString("macAdress", "none");
        if(macAdress.equals("none")){

            macAdress = getString(R.string.my_mac);


        }
        editText.setText(macAdress);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                macAdress = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        saveAdress();
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveAdress();
        super.onStop();

    }
    private void saveAdress(){
        if(macAdress != null){

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("macAdress",macAdress);
            editor.commit();

        }

    }

    public void sendMagicPacket (View view) throws SocketException , IOException{

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pref = sharedPreferences.getString("home_ssid",null);
        if(pref != null){

            Log.d("main", pref);

        }

        //TODO try catch here
        Intent serviceIntent = new Intent(getApplicationContext(),WOLService.class);
        String[] array = {macAdress};

        //TODO get from saved data

        serviceIntent.putExtra("macAdresses",array);

        startService(serviceIntent);

    }

}

