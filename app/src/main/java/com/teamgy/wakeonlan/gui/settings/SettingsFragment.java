package com.teamgy.wakeonlan.gui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

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
        //reveals or hides quiet hours depending on quietHoursEnabledSwitch
        SwitchPreference quietHoursEnabledSwitch = (SwitchPreference)findPreference(getString(R.string.key_quiet_hours_enabled));

        Preference timeStart = findPreference(getString(R.string.key_time_start));
        Preference timeEnd = findPreference(getString(R.string.key_time_end));

        timeStart.setEnabled(quietHoursEnabledSwitch.isChecked());
        timeEnd.setEnabled(quietHoursEnabledSwitch.isChecked());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if(key.equals(getString(R.string.key_quiet_hours_enabled)) ){
            if(pref instanceof SwitchPreference){
                //get 2 other preferences in auto wol category
                Preference timeStart = findPreference(getString(R.string.key_time_start));
                Preference timeEnd = findPreference(getString(R.string.key_time_end));

                timeEnd.setEnabled(((SwitchPreference) pref).isChecked());
                timeStart.setEnabled(((SwitchPreference) pref).isChecked());
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
