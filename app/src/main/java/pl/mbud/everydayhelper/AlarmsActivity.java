package pl.mbud.everydayhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.mbud.everydayhelper.adapters.AlarmListAdapter;
import pl.mbud.everydayhelper.alarm.EventNotificationAlarmManager;
import pl.mbud.everydayhelper.alarm.MyAlarmManager;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;

/**
 * Created by Maciek on 02.01.2017.
 */

public class AlarmsActivity extends BaseActivity {

    private List<AlarmData> alarmList;
    private AlarmListAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private TextView noAlarmsView;

    private Button addAlarmButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_list);

        recyclerView = (RecyclerView) findViewById(R.id.alarm_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmList = new ArrayList<>();
        adapter = new AlarmListAdapter(alarmList, this);
        recyclerView.setAdapter(adapter);
        addAlarmButton = (Button) findViewById(R.id.activity_alarm_list_add_alarm);
        noAlarmsView = (TextView) findViewById(R.id.no_alarms);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmsActivity.this, AddAlarmActivity.class);
                startActivity(intent);
            }
        });
        adapter.addOnItemClickListener(new RecyclerViewItemClickListener<AlarmData>() {
            @Override
            public void onItemClicked(View view, AlarmData item, int position) {
                Intent intent = new Intent(AlarmsActivity.this, AddAlarmActivity.class);
                intent.putExtra(AddAlarmActivity.EXTRA_ALARM_ID, item.getAlarmId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(View view, final AlarmData alarm, int position) {
                PopupMenu menu = new PopupMenu(AlarmsActivity.this, view);
                menu.getMenuInflater().inflate(R.menu.menu_alarm_list, menu.getMenu());

                if (alarm.isEnabled()) {
                    menu.getMenu().findItem(R.id.menu_alarm_item_enable).setVisible(false);
                }
                else {
                    menu.getMenu().findItem(R.id.menu_alarm_item_disable).setVisible(false);
                }

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_alarm_item_enable:
                                enableAlarm(alarm);
                                return true;
                            case R.id.menu_alarm_item_disable:
                                disableAlarm(alarm);
                                return true;
                            case R.id.menu_alarm_item_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmsActivity.this);
                                builder.setTitle(getString(R.string.confirm_deletion));
                                builder.setMessage(getString(R.string.are_you_sure_delete_alarm))
                                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deleteAlarm(alarm);
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                builder.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                menu.show();
            }
        });

        dbHelper = new DatabaseHelper(this);
        fetchAlarms();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchAlarms();
    }

    private void fetchAlarms() {
        alarmList.clear();
        dbHelper.open();
        alarmList.addAll(dbHelper.getAlarms());
        dbHelper.close();
        if (alarmList.isEmpty()) {
            noAlarmsView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            noAlarmsView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void enableAlarm(AlarmData data) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        data.setEnabled(true);
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(data.getDate());
        while (c.getTime().before(now)) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        data.setDate(c.getTime());
        dbHelper.updateAlarm(data);
        dbHelper.close();
        fetchAlarms();
        MyAlarmManager manager = new MyAlarmManager(this);
        manager.scheduleAlarm(data);
    }

    private void disableAlarm(AlarmData data) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        data.setEnabled(false);
        dbHelper.updateAlarm(data);
        dbHelper.close();
        fetchAlarms();
        MyAlarmManager manager = new MyAlarmManager(this);
        manager.cancelAlarm(data);
    }

    private void deleteAlarm(AlarmData data) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        dbHelper.deleteAlarm(data.getAlarmId());
        dbHelper.close();
        fetchAlarms();
        MyAlarmManager manager = new MyAlarmManager(this);
        manager.cancelAlarm(data);
    }

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_alarm;
    }
}
