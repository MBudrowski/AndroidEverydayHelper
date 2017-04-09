package pl.mbud.everydayhelper.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.mbud.everydayhelper.AlarmActivity;
import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.EventInfoActivity;
import pl.mbud.everydayhelper.NotificationPublisher;
import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.data.WeekDay;
import pl.mbud.everydayhelper.database.DatabaseHelper;

/**
 * Created by Maciek on 02.01.2017.
 */

public class EventNotificationAlarmManager {
    public static final String DEBUG_TAG = "EVENT-ALARMS";

    private Context context;

    public EventNotificationAlarmManager(Context context) {
        this.context = context;
    }

    public void scheduleEventNotificationAlarm(EventData data) {
        if (data.getId() == 0) {
            return;
        }
        if (data.getMinutesBeforeNotification() == 0 || data.getDate().before(new Date())) {
            return;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(data.getDate());
        c.add(Calendar.MINUTE, -data.getMinutesBeforeNotification());

        Intent alarmIntent = new Intent(context, NotificationPublisher.class);
        alarmIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION_ID, data.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, data.getId(), alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Notification alarm set: " + data.getName() + " at " + c.getTime().toString());
    }

    public void cancelEventNotificationAlarm(EventData data) {
        if (data.getId() == null || data.getId() == 0) {
            return;
        }
        Intent alarmIntent = new Intent(context, NotificationPublisher.class);
        alarmIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION_ID, data.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, data.getId(), alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Notification alarm cancelled: " + data.getName());
    }

    public void scheduleAllNotifications() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.open();
        List<EventData> events = dbHelper.getEventsFuture();
        Date now = new Date();
        for (EventData data : events) {
            if (data.getDate().before(now)) {
                continue;
            }
            scheduleEventNotificationAlarm(data);
        }
        dbHelper.close();
    }
}
