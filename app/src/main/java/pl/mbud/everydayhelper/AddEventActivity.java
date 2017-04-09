package pl.mbud.everydayhelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import pl.mbud.everydayhelper.alarm.EventNotificationAlarmManager;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.AlarmShutOffMethod;
import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.database.DatabaseHelper;

public class AddEventActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "EventID";
    public static final String EXTRA_EVENT_DAY = "Day";
    public static final String EXTRA_EVENT_MONTH = "Month";
    public static final String EXTRA_EVENT_YEAR = "Year";

    private Button dateButton, timeButton, notifyButton, addEventButton;
    private TextView dateText, timeText, notifyText;
    private EditText nameEditText, descEditText;

    private Integer selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private int selectedTimeNotify = 0, selectedTimeNotifyIndex = 0;
    private EventData editedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        int editedId = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        if (editedId != 0) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.open();
            editedEvent = dbHelper.getEvent(editedId);
            dbHelper.close();
        }
        int day = getIntent().getIntExtra(EXTRA_EVENT_DAY, -1);
        int month = getIntent().getIntExtra(EXTRA_EVENT_MONTH, -1);
        int year = getIntent().getIntExtra(EXTRA_EVENT_YEAR, -1);

        dateButton = (Button) findViewById(R.id.activity_add_event_date_button);
        timeButton = (Button) findViewById(R.id.activity_add_event_time_button);
        addEventButton = (Button) findViewById(R.id.activity_add_event_add_event);
        dateText = (TextView) findViewById(R.id.activity_add_event_date);
        timeText = (TextView) findViewById(R.id.activity_add_event_time);
        nameEditText = (EditText) findViewById(R.id.activity_add_event_name);
        descEditText = (EditText) findViewById(R.id.activity_add_event_desc);
        notifyButton = (Button) findViewById(R.id.activity_add_event_notify_button);
        notifyText = (TextView) findViewById(R.id.activity_add_event_notify);

        if (day != -1 && month != -1 && year != -1) {
            selectedDay = day;
            selectedMonth = month;
            selectedYear = year;
            dateText.setText(String.format("%02d", selectedDay) + "." + String.format("%02d", selectedMonth + 1) + "." + String.format("%02d", selectedYear));
        }

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog dialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        selectedHour = h;
                        selectedMinute = m;
                        timeText.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, (selectedHour == null) ? 12 : selectedHour, (selectedMinute == null) ? 0 : selectedMinute, true);
                dialog.show();
            }
        });
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        selectedDay = d;
                        selectedMonth = m;
                        selectedYear = y;
                        dateText.setText(String.format("%02d", selectedDay) + "." + String.format("%02d", selectedMonth + 1) + "." + String.format("%02d", selectedYear));
                    }
                }, (selectedYear == null) ? c.get(Calendar.YEAR) : selectedYear, (selectedMonth == null) ? c.get(Calendar.MONTH) : selectedMonth,
                        (selectedDay == null) ? c.get(Calendar.DAY_OF_MONTH) : selectedDay);
                dialog.show();
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddEventActivity.this);
                builder.setTitle(getString(R.string.choose_when_the_notification));
                final String[] items = getResources().getStringArray(R.array.notifyTime);
                final int[] values = getResources().getIntArray(R.array.notifyTimeValue);
                final int actualTime = selectedTimeNotify;
                final int actualIndex = selectedTimeNotifyIndex;
                builder.setSingleChoiceItems(items, selectedTimeNotifyIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedTimeNotifyIndex = i;
                        selectedTimeNotify = values[i];
                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notifyText.setText(items[selectedTimeNotifyIndex]);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedTimeNotify = actualTime;
                        selectedTimeNotifyIndex = actualIndex;
                        dialogInterface.dismiss();
                    }
                });
                Dialog d = builder.create();
                d.show();
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });

        if (editedEvent != null) {
            nameEditText.setText(editedEvent.getName());
            descEditText.setText(editedEvent.getDesc());
            c.setTime(editedEvent.getDate());
            selectedYear = c.get(Calendar.YEAR);
            selectedMonth = c.get(Calendar.MONTH);
            selectedDay = c.get(Calendar.DAY_OF_MONTH);
            selectedHour = c.get(Calendar.HOUR_OF_DAY);
            selectedMinute = c.get(Calendar.MINUTE);
            timeText.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
            dateText.setText(String.format("%02d", selectedDay) + "." + String.format("%02d", selectedMonth + 1) + "." + String.format("%02d", selectedYear));
            selectedTimeNotify = editedEvent.getMinutesBeforeNotification();
            final String[] items = getResources().getStringArray(R.array.notifyTime);
            final int[] values = getResources().getIntArray(R.array.notifyTimeValue);
            for (int i = 0; i < values.length; i++) {
                if (values[i] == selectedTimeNotify) {
                    selectedTimeNotifyIndex = i;
                    break;
                }
            }
            notifyText.setText(items[selectedTimeNotifyIndex]);
        }
    }

    private void addEvent() {
        String name = nameEditText.getText().toString();
        if (name.isEmpty()) {
            nameEditText.setError(getString(R.string.field_required));
            nameEditText.requestFocus();
            return;
        }
        String desc = descEditText.getText().toString();
        if (desc.isEmpty()) {
            descEditText.setError(getString(R.string.field_required));
            descEditText.requestFocus();
            return;
        }
        if (selectedYear == null || selectedMonth == null || selectedDay == null) {
            Toast.makeText(AddEventActivity.this, getString(R.string.didnt_choose_date), Toast.LENGTH_LONG).show();
            return;
        }
        if (selectedHour == null || selectedMinute == null) {
            Toast.makeText(AddEventActivity.this, getString(R.string.didnt_choose_time), Toast.LENGTH_LONG).show();
            return;
        }
        EventNotificationAlarmManager manager = new EventNotificationAlarmManager(this);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.YEAR, selectedYear);
        c.set(Calendar.MONTH, selectedMonth);
        c.set(Calendar.DAY_OF_MONTH, selectedDay);
        c.set(Calendar.HOUR_OF_DAY, selectedHour);
        c.set(Calendar.MINUTE, selectedMinute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        EventData data;
        if (editedEvent != null) {
            data = editedEvent;
        } else {
            data = new EventData();
        }
        manager.cancelEventNotificationAlarm(data);
        data.setName(name);
        data.setDesc(desc);
        data.setDate(c.getTime());
        data.setMinutesBeforeNotification(selectedTimeNotify);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        if (editedEvent == null) {
            data.setId((int) dbHelper.insertEvent(data));
        } else {
            dbHelper.updateEvent(data);
        }
        dbHelper.close();

        manager.scheduleEventNotificationAlarm(data);

        finish();
    }

}
