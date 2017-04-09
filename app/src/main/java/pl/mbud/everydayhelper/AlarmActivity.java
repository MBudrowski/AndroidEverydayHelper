package pl.mbud.everydayhelper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import pl.mbud.everydayhelper.alarm.MyAlarmManager;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.WeekDay;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.fragments.alarm.AlarmShutOffFragment;
import pl.mbud.everydayhelper.fragments.alarm.ButtonFragment;
import pl.mbud.everydayhelper.fragments.alarm.MathFragment;
import pl.mbud.everydayhelper.fragments.alarm.ShakeFragment;

/**
 * Created by Maciek on 02.01.2017.
 */

public class AlarmActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ALARM-ACT";
    public static final String EXTRA_ID_ALARM = "AlarmID";
    private MediaPlayer ringtonePlayer;

    private TextView alarmNameTextView;
    private AlarmData data;
    private Vibrator vibrator;
    private long[] vibratePattern = {0, 500, 500};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        alarmNameTextView = (TextView) findViewById(R.id.alarm_name);

        int id = getIntent().getIntExtra(EXTRA_ID_ALARM, 0);
        if (id == 0) {
            finish();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        data = dbHelper.getAlarm(id);
        dbHelper.close();

        alarmNameTextView.setText(data.getName());

        AlarmShutOffFragment fragment = null;
        switch (data.getShutOffMethod()) {
            case BUTTON:
                fragment = new ButtonFragment();
                break;
            case MATH:
                fragment = new MathFragment();
                break;
            case SHAKE:
                if (BaseApplication.getInstance().hasAccelerometer()) {
                    fragment = new ShakeFragment();
                } else {
                    fragment = new ButtonFragment();
                }
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.alarm_fragment_container, fragment);
        transaction.commit();

        if (data.getRingtone() != null) {
            playRingtone(Uri.parse(data.getRingtone()), data.getRingtoneVolume());
        }

        if (vibrator != null) {
            vibrator.vibrate(vibratePattern, 0);
        }
    }

    private void playRingtone(Uri ringtoneUri, int volume) {
        if (ringtonePlayer != null) {
            return;
        }
        try {
            ringtonePlayer = new MediaPlayer();
            ringtonePlayer.setDataSource(this, ringtoneUri);
            ringtonePlayer.prepare();
            ringtonePlayer.setLooping(true);
            float log1 = (float) (Math.log(100 - volume) / Math.log(100));
            ringtonePlayer.setVolume(1 - log1, 1 - log1);
            ringtonePlayer.start();
        } catch (IOException e) {
            Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
        }
    }

    private void stopRingtone() {
        if (ringtonePlayer != null) {
            if (ringtonePlayer.isPlaying()) {
                ringtonePlayer.stop();
            }
            ringtonePlayer.reset();
            ringtonePlayer.release();
            ringtonePlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (data.isRepeating()) {
            Date now = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(data.getDate());
            while (c.getTime().before(now) || !data.isRepeating(WeekDay.valueOf(c.get(Calendar.DAY_OF_WEEK)))) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            data.setDate(c.getTime());
            MyAlarmManager manager = new MyAlarmManager(this);
            manager.scheduleAlarm(data);
        } else {
            data.setEnabled(false);
        }
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        dbHelper.updateAlarm(data);
        dbHelper.close();
        stopRingtone();
        vibrator.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
