package pl.mbud.everydayhelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pl.mbud.everydayhelper.alarm.MyAlarmManager;
import pl.mbud.everydayhelper.alarm.WeatherUpdateAlarmManager;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.events.PreferenceChangeEvent;

/**
 * Created by Maciek on 30.12.2016.
 */

public class BaseApplication extends Application {

    public static final String DEBUG_TAG = "EVERYDAY-HELPER";

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private WeatherUpdateAlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DatabaseHelper(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alarmManager = new WeatherUpdateAlarmManager(getApplicationContext());
        alarmManager.scheduleAlarm();
        MyAlarmManager myAlarmManager = new MyAlarmManager(getApplicationContext());
        myAlarmManager.scheduleAllEnabledAlarms();

        instance = this;

        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        EventBus.getDefault().unregister(this);
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                Log.d(BaseApplication.DEBUG_TAG, Log.getStackTraceString(e));
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public boolean hasAccelerometer() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null) {
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreferencesChanged(PreferenceChangeEvent event) {
        alarmManager.scheduleAlarm();
    }
}
