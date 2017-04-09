package pl.mbud.everydayhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.database.DatabaseHelper;

/**
 * Created by Maciek on 05.01.2017.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public static String EXTRA_NOTIFICATION_ID = "notification-id";

    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.open();
        EventData data = dbHelper.getEvent(id);
        dbHelper.close();
        if (data == null) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(data.getName());
        builder.setContentText(context.getString(R.string.upcoming_event));
        builder.setSmallIcon(R.drawable.icon_notification);
        builder.setAutoCancel(true);
        builder.setVibrate(new long[] {0l, 500l, 0l, 500l});
        builder.setWhen(data.getDate().getTime());
        builder.setShowWhen(true);
        Intent resultIntent = new Intent(context, EventInfoActivity.class);
        resultIntent.putExtra(EventInfoActivity.EXTRA_EVENT_ID, data.getId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EventInfoActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(id, builder.build());

    }
}