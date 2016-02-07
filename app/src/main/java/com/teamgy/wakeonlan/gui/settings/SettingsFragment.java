package com.teamgy.wakeonlan.gui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.teamgy.wakeonlan.R;

/**
 * Created by Jakov on 31/10/2015.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        loadHiddenPreferencesState();
    }

    private void loadHiddenPreferencesState() {
        //reveals or hides quiet hours dpedning on checkbox
        CheckBoxPreference checkbox = (CheckBoxPreference)findPreference(getString(R.string.key_quiet_hours_enabled));

        Preference timeStart = findPreference(getString(R.string.key_time_start));
        Preference timeEnd = findPreference(getString(R.string.key_time_end));

        timeStart.setEnabled(checkbox.isChecked());
        timeEnd.setEnabled(checkbox.isChecked());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if(key.equals(getString(R.string.key_quiet_hours_enabled)) ){
            if(pref instanceof CheckBoxPreference){
                //get 2 other preferences in auto wol category
                Preference timeStart = findPreference(getString(R.string.key_time_start));
                Preference timeEnd = findPreference(getString(R.string.key_time_end));

                timeEnd.setEnabled(((CheckBoxPreference) pref).isChecked());
                timeStart.setEnabled(((CheckBoxPreference) pref).isChecked());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
