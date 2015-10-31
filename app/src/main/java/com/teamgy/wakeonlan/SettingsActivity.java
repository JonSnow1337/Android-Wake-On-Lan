package com.teamgy.wakeonlan;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Jakov on 31/10/2015.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
