package pl.mbud.everydayhelper.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.WeatherService;

/**
 * Created by Maciek on 01.01.2017.
 */

public class WeatherUpdateAlarmManager {
    public static final String DEBUG_TAG = "WEATHER-ALARMS";

    private Context context;
    private PendingIntent pendingIntent;
    private SharedPreferences preferences;

    public WeatherUpdateAlarmManager(Context context) {
        this.context = context;
        Intent alarmIntent = new Intent(context, WeatherService.class);
        pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void scheduleAlarm() {
        Date alarmBaseTime = new Date(preferences.getLong("weatherUpdateTime", 0l));
        int updateRate = Integer.parseInt(preferences.getString("weatherUpdateRate", "0"));
        if (updateRate > 0) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Date now = new Date();
            Calendar baseCalendar = Calendar.getInstance();
            baseCalendar.setTime(alarmBaseTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, baseCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, baseCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);

            while (now.before(calendar.getTime())) {
                calendar.add(Calendar.MINUTE, -updateRate);
            }

            while (now.after(calendar.getTime())) {
                calendar.add(Calendar.MINUTE, updateRate);
            }

            manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    1000 * 60 * updateRate, pendingIntent);

            Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Weather update alarm scheduled at " + calendar.getTime().toString());
        }
        else {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Weather update alarm canceled");
        }
    }
}
