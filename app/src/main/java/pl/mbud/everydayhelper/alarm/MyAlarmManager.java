package pl.mbud.everydayhelper.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.mbud.everydayhelper.AlarmActivity;
import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.WeekDay;
import pl.mbud.everydayhelper.database.DatabaseHelper;

/**
 * Created by Maciek on 02.01.2017.
 */

public class MyAlarmManager {
    public static final String DEBUG_TAG = "ALARMS";

    private Context context;

    public MyAlarmManager(Context context) {
        this.context = context;
    }

    public void scheduleAlarm(AlarmData data) {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(AlarmActivity.EXTRA_ID_ALARM, data.getAlarmId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, data.getAlarmId(), alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, data.getDate().getTime(), pendingIntent);
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Alarm set: " + data.getName() + " at " + data.getDate().toString());
    }

    public void cancelAlarm(AlarmData data) {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(AlarmActivity.EXTRA_ID_ALARM, data.getAlarmId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, data.getAlarmId(), alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Alarm cancelled: " + data.getName());
    }

    public void scheduleAllEnabledAlarms() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.open();
        List<AlarmData> alarms = dbHelper.getAllEnabledAlarms();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        for (AlarmData data : alarms) {
            if (!data.isEnabled()) {
                continue;
            }
            if (data.getDate().before(now)) {
                if (!data.isRepeating()) {
                    data.setEnabled(false);
                    dbHelper.updateAlarm(data);
                } else {
                    c.setTime(data.getDate());
                    while (c.getTime().before(now) || !data.isRepeating(WeekDay.valueOf(c.get(Calendar.DAY_OF_WEEK)))) {
                        c.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    data.setDate(c.getTime());
                    dbHelper.updateAlarm(data);
                    scheduleAlarm(data);
                }
                continue;
            } else {
                scheduleAlarm(data);
            }
        }
        dbHelper.close();
    }
}
