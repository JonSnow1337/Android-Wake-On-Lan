package com.teamgy.wakeonlan.gui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.teamgy.wakeonlan.R;

/**
 * Created by Jakov on 31/10/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}