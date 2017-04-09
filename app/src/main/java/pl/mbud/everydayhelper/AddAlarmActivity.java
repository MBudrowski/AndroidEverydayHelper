package pl.mbud.everydayhelper;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import pl.mbud.everydayhelper.alarm.MyAlarmManager;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.AlarmShutOffMethod;
import pl.mbud.everydayhelper.data.WeekDay;
import pl.mbud.everydayhelper.database.DatabaseHelper;

/**
 * Created by Maciek on 02.01.2017.
 */

public class AddAlarmActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ADD-ALARM-ACT";
    public static final String EXTRA_ALARM_ID = "AlarmID";

    private EditText nameEditText;
    private Button timeButton, repeatButton, ringtoneButton, shutOffButton;
    private TextView timeTextView, repeatTextView, ringtoneTextView, shutOffTextView;
    private SeekBar volumeBar;
    private Button addAlarmButton;

    private Uri[] ringtoneUris;
    private Ringtone[] ringtones;
    private int numOfRingtones = 0;
    private Integer selectedHour = null, selectedMinute = null;
    private Integer tmpHour = 0, tmpMinute = 0;
    private String selectedRingtone = null;
    private String selectedRingtoneUri = null;
    private int ringtoneDialogSelected = 0;
    private boolean[] daysChecked = new boolean[7];
    private boolean[] tmpDaysChecked = new boolean[7];
    private AlarmShutOffMethod shutOffMethod = AlarmShutOffMethod.valueOf(0);

    private Handler volumeBarRingtoneHandler = new Handler();
    private MediaPlayer ringtonePlayer;

    private int editedAlarmId = 0;
    private AlarmData editedAlarm;

    private static final int MAX_VOLUME = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_alarm);

        editedAlarmId = getIntent().getIntExtra(EXTRA_ALARM_ID, 0);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        editedAlarm = dbHelper.getAlarm(editedAlarmId);
        dbHelper.close();

        int[] daysArray = getResources().getIntArray(R.array.weekDaysValues);
        if (editedAlarm != null) {
            for (int i = 0; i < daysChecked.length; i++) {
                daysChecked[i] = tmpDaysChecked[i] = editedAlarm.isRepeating(WeekDay.valueOf(daysArray[i]));
            }
        } else {
            for (int i = 0; i < daysChecked.length; i++) {
                daysChecked[i] = tmpDaysChecked[i] = false;
            }
        }
        loadRingtones();

        nameEditText = (EditText) findViewById(R.id.activity_add_alarm_name);
        timeTextView = (TextView) findViewById(R.id.activity_add_alarm_time);
        timeButton = (Button) findViewById(R.id.activity_add_alarm_time_button);
        ringtoneTextView = (TextView) findViewById(R.id.activity_add_alarm_ringtone);
        ringtoneButton = (Button) findViewById(R.id.activity_add_alarm_ringtone_button);
        repeatTextView = (TextView) findViewById(R.id.activity_add_alarm_repeat);
        repeatButton = (Button) findViewById(R.id.activity_add_alarm_repeat_button);
        shutOffTextView = (TextView) findViewById(R.id.activity_add_alarm_shut_off);
        shutOffButton = (Button) findViewById(R.id.activity_add_alarm_shut_off_button);
        volumeBar = (SeekBar) findViewById(R.id.activity_add_alarm_volume_bar);
        addAlarmButton = (Button) findViewById(R.id.activity_add_alarm_add_alarm);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog dialog = new TimePickerDialog(AddAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        selectedHour = h;
                        selectedMinute = m;
                        timeTextView.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, (selectedHour == null) ? 12 : selectedHour, (selectedMinute == null) ? 0 : selectedMinute, true);
                dialog.show();
            }
        });
        ringtoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRingtone();
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddAlarmActivity.this);
                builder.setTitle(getString(R.string.choose_ringtone_short));
                final CharSequence[] items = new CharSequence[numOfRingtones + 1];
                items[0] = getString(R.string.none);
                int i = 1;
                for (int j = 0; j < numOfRingtones; j++) {
                    items[i++] = ringtones[j].getTitle(AddAlarmActivity.this);
                }
                builder.setSingleChoiceItems(items, ringtoneDialogSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopRingtone();
                        ringtoneDialogSelected = i;
                        if (ringtoneDialogSelected != 0) {
                            playRingtone(ringtoneUris[ringtoneDialogSelected - 1], MAX_VOLUME);
                        }
                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopRingtone();
                        if (ringtoneDialogSelected == 0) {
                            selectedRingtone = null;
                            selectedRingtoneUri = null;
                            ringtoneTextView.setText(getString(R.string.none));
                        } else {
                            selectedRingtone = ringtones[ringtoneDialogSelected - 1].getTitle(getApplicationContext());
                            selectedRingtoneUri = ringtoneUris[ringtoneDialogSelected - 1].toString();
                            ringtoneTextView.setText(selectedRingtone);
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopRingtone();
                        dialogInterface.dismiss();
                    }
                });
                Dialog d = builder.create();
                d.show();
            }
        });
        volumeBar.setMax(MAX_VOLUME);
        volumeBar.setProgress((editedAlarm != null) ? editedAlarm.getRingtoneVolume() : MAX_VOLUME);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                     if (ringtoneDialogSelected != 0) {
                                                         volumeBarRingtoneHandler.removeCallbacksAndMessages(null);
                                                         if (ringtonePlayer == null) {
                                                             playRingtone(Uri.parse(selectedRingtoneUri), i);
                                                         } else {
                                                             setPlayerVolume(i);
                                                         }
                                                         stopRingtoneDelayed(5000);
                                                     }
                                                 }

                                                 @Override
                                                 public void onStartTrackingTouch(SeekBar seekBar) {

                                                 }

                                                 @Override
                                                 public void onStopTrackingTouch(SeekBar seekBar) {

                                                 }
                                             }

        );

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRingtone();
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddAlarmActivity.this);
                builder.setTitle(getString(R.string.choose_days_short));
                final CharSequence[] items = new CharSequence[7];
                final int[] daysArray = getResources().getIntArray(R.array.weekDaysValues);
                for (int i = 0; i < daysArray.length; i++) {
                    items[i] = WeekDay.valueOf(daysArray[i]).getName(getApplicationContext());
                }
                builder.setMultiChoiceItems(items, tmpDaysChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        tmpDaysChecked[i] = b;
                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder sb = new StringBuilder();
                        boolean notFirst = false;
                        for (int j = 0; j < daysChecked.length; j++) {
                            daysChecked[j] = tmpDaysChecked[j];
                            if (daysChecked[j]) {
                                if (notFirst) {
                                    sb.append(", " + items[j]);
                                } else {
                                    sb.append(items[j]);
                                    notFirst = true;
                                }
                            }
                        }
                        String s = sb.toString();
                        if (s.isEmpty()) {
                            s = getString(R.string.no_repeat);
                        }
                        repeatTextView.setText(s);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < daysChecked.length; j++) {
                            tmpDaysChecked[j] = daysChecked[j];
                        }
                        dialogInterface.dismiss();
                    }
                });
                Dialog d = builder.create();
                d.show();
            }
        });

        shutOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRingtone();
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddAlarmActivity.this);
                builder.setTitle(getString(R.string.choose_shut_off_method_short));
                final AlarmShutOffMethod[] methods = AlarmShutOffMethod.values();
                final CharSequence[] items = new CharSequence[methods.length];
                final AlarmShutOffMethod actualMethod = shutOffMethod;
                int i = 0;
                for (AlarmShutOffMethod m : methods) {
                    items[i++] = m.getName(AddAlarmActivity.this);
                }
                builder.setSingleChoiceItems(items, shutOffMethod.getValue(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shutOffMethod = methods[i];
                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shutOffTextView.setText(shutOffMethod.getName(AddAlarmActivity.this));
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shutOffMethod = actualMethod;
                        dialogInterface.dismiss();
                    }
                });
                Dialog d = builder.create();
                d.show();
            }
        });

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlarm();
            }
        });

        if (editedAlarm != null) {
            nameEditText.setText(editedAlarm.getName());

            Calendar c = Calendar.getInstance();
            c.setTime(editedAlarm.getDate());
            selectedHour = c.get(Calendar.HOUR_OF_DAY);
            selectedMinute = c.get(Calendar.MINUTE);
            timeTextView.setText(selectedHour + ":" + String.format("%02d", selectedMinute));

            selectedRingtoneUri = editedAlarm.getRingtone();
            if (selectedRingtoneUri == null) {
                selectedRingtone = null;
                ringtoneDialogSelected = 0;
                ringtoneTextView.setText(getString(R.string.none));
            } else {
                for (int i = 0; i < numOfRingtones; i++) {
                    if (ringtoneUris[i].toString().equals(selectedRingtoneUri)) {
                        selectedRingtone = ringtones[i].getTitle(this);
                        ringtoneDialogSelected = i + 1;
                        ringtoneTextView.setText(selectedRingtone);
                        break;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            boolean notFirst = false;
            for (int j = 0; j < daysChecked.length; j++) {
                daysChecked[j] = tmpDaysChecked[j];
                if (daysChecked[j]) {
                    if (notFirst) {
                        sb.append(", " + WeekDay.valueOf(daysArray[j]).getName(this));
                    } else {
                        sb.append(WeekDay.valueOf(daysArray[j]).getName(this));
                        notFirst = true;
                    }
                }
            }
            String s = sb.toString();
            if (s.isEmpty()) {
                s = getString(R.string.no_repeat);
            }

            shutOffTextView.setText(editedAlarm.getShutOffMethod().getName(this));
            shutOffMethod = editedAlarm.getShutOffMethod();

            repeatTextView.setText(s);
        }
    }

    private void addAlarm() {
        String s = nameEditText.getText().toString();
        if (s.isEmpty()) {
            nameEditText.setError(getString(R.string.field_required));
            nameEditText.requestFocus();
            return;
        }
        if (selectedHour == null || selectedMinute == null) {
            Toast.makeText(this, getString(R.string.didnt_choose_time), Toast.LENGTH_LONG).show();
            return;
        }
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, selectedHour);
        c.set(Calendar.MINUTE, selectedMinute);
        c.set(Calendar.SECOND, 0);
        int repeatMode = 0;
        int[] daysArray = getResources().getIntArray(R.array.weekDaysValues);
        for (int i = 0; i < 7; i++) {
            if (daysChecked[i]) {
                repeatMode |= (1 << (daysArray[i] - 1));
            }
        }
        if (repeatMode == 0) {
            while (c.getTime().before(now)) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            while (c.getTime().before(now) || ((repeatMode >> (c.get(Calendar.DAY_OF_WEEK) - 1)) & 0x01) == 0) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        AlarmData data;
        if (editedAlarm != null) {
            data = editedAlarm;
        } else {
            data = new AlarmData();
        }
        data.setName(s);
        data.setDate(c.getTime());
        data.setRepeatMode(repeatMode);
        data.setRingtone(selectedRingtoneUri);
        data.setRingtoneVolume(volumeBar.getProgress());
        data.setEnabled(true);
        data.setShutOffMethod(shutOffMethod);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        if (editedAlarm != null) {
            dbHelper.updateAlarm(data);
        } else {
            data.setAlarmId((int) dbHelper.insertAlarm(data));
        }
        dbHelper.close();

        MyAlarmManager alarm = new MyAlarmManager(this);
        alarm.scheduleAlarm(data);

        finish();
    }

    private void loadRingtones() {
        RingtoneManager ringtoneMgr = new RingtoneManager(this);
        ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor alarmsCursor = ringtoneMgr.getCursor();
        int alarmsCount = alarmsCursor.getCount();
        if (alarmsCount != 0) {
            ringtones = new Ringtone[alarmsCount];
            ringtoneUris = new Uri[alarmsCount];
            alarmsCursor.moveToFirst();
            int i = 0;
            Ringtone r;
            while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
                int currentPosition = alarmsCursor.getPosition();
                ringtoneUris[i] = ringtoneMgr.getRingtoneUri(currentPosition);
                r = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUris[i]);
                if (r != null) {
                    ringtones[i] = r;
                    i++;
                }
            }
            numOfRingtones = i;
            alarmsCursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void playRingtone(Uri ringtoneUri, int volume) {
        if (ringtonePlayer != null) {
            return;
        }
        try {
            ringtonePlayer = new MediaPlayer();
            ringtonePlayer.setDataSource(this, ringtoneUri);
            ringtonePlayer.prepare();
            ringtonePlayer.setLooping(false);
            float log1 = (float) (Math.log(100 - volume) / Math.log(100));
            ringtonePlayer.setVolume(1 - log1, 1 - log1);
            ringtonePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (ringtonePlayer.isPlaying()) {
                        ringtonePlayer.stop();
                    }
                    ringtonePlayer.reset();
                    ringtonePlayer.release();
                    ringtonePlayer = null;
                }
            });
            ringtonePlayer.start();
        } catch (IOException e) {
            Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
        }
    }

    private void stopRingtone() {
        volumeBarRingtoneHandler.removeCallbacksAndMessages(null);
        if (ringtonePlayer != null) {
            if (ringtonePlayer.isPlaying()) {
                ringtonePlayer.stop();
            }
            ringtonePlayer.reset();
            ringtonePlayer.release();
            ringtonePlayer = null;
        }
    }

    private void setPlayerVolume(int volume) {
        float log1 = (float) (Math.log(100 - volume) / Math.log(100));
        ringtonePlayer.setVolume(1 - log1, 1 - log1);
    }

    private void stopRingtoneDelayed(int delay) {
        volumeBarRingtoneHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ringtonePlayer == null) {
                    return;
                }
                if (ringtonePlayer.isPlaying()) {
                    ringtonePlayer.stop();
                }
                ringtonePlayer.reset();
                ringtonePlayer.release();
                ringtonePlayer = null;
            }
        }, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        volumeBarRingtoneHandler.removeCallbacksAndMessages(null);
        if (ringtonePlayer != null) {
            if (ringtonePlayer.isPlaying()) {
                ringtonePlayer.stop();
            }
            ringtonePlayer.reset();
            ringtonePlayer.release();
            ringtonePlayer = null;
        }
    }
}
