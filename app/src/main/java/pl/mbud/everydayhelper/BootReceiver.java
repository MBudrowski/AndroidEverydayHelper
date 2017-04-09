package pl.mbud.everydayhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import pl.mbud.everydayhelper.alarm.EventNotificationAlarmManager;
import pl.mbud.everydayhelper.alarm.MyAlarmManager;
import pl.mbud.everydayhelper.alarm.WeatherUpdateAlarmManager;

/**
 * Created by Maciek on 31.12.2016.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            WeatherUpdateAlarmManager alarmManager = new WeatherUpdateAlarmManager(context);
            alarmManager.scheduleAlarm();

            MyAlarmManager myAlarmManager = new MyAlarmManager(context);
            myAlarmManager.scheduleAllEnabledAlarms();

            EventNotificationAlarmManager eventNotificationAlarmManager = new EventNotificationAlarmManager(context);
            eventNotificationAlarmManager.scheduleAllNotifications();
        }
    }
}
