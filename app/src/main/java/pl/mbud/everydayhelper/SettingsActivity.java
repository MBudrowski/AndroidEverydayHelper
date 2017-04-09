package pl.mbud.everydayhelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import pl.mbud.everydayhelper.events.PreferenceChangeEvent;
import pl.mbud.everydayhelper.util.AppCompatPreferenceActivity;

/**
 * Created by Maciek on 31.12.2016.
 */

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String DEBUG_TAG = "PREFERENCES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Preference changed: " + s);
        if (s.equals("weatherUpdateTime") || s.equals("weatherUpdateRate")) {
            EventBus.getDefault().post(new PreferenceChangeEvent(s));
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}