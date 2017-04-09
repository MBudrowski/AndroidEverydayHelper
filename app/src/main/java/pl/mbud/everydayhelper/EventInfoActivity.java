package pl.mbud.everydayhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.mbud.everydayhelper.alarm.EventNotificationAlarmManager;
import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.database.DatabaseHelper;

public class EventInfoActivity extends BaseActivity {

    public static final String EXTRA_EVENT_ID = "EventId";
    private TextView nameText, descText, dateText;
    private Button editButton, deleteButton, backButton;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm, dd-MM-yyyy");
    private EventData eventData;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        if (eventId == 0) {
            finish();
            return;
        }


        nameText = (TextView) findViewById(R.id.event_name_text);
        descText = (TextView) findViewById(R.id.event_desc_text);
        dateText = (TextView) findViewById(R.id.event_date_text);

        editButton = (Button) findViewById(R.id.activity_event_info_edit);
        deleteButton = (Button) findViewById(R.id.activity_event_info_delete);
        backButton = (Button) findViewById(R.id.activity_event_info_calendar);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventInfoActivity.this, AddEventActivity.class);
                intent.putExtra(AddEventActivity.EXTRA_EVENT_ID, eventData.getId());
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventInfoActivity.this);
                builder.setTitle(getString(R.string.confirm_deletion));
                builder.setMessage(getString(R.string.are_you_sure_delete_event))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHelper dbHelper = new DatabaseHelper(EventInfoActivity.this);
                                dbHelper.open();
                                dbHelper.deleteEvent(eventData.getId());
                                dbHelper.close();
                                dialogInterface.dismiss();
                                EventNotificationAlarmManager manager = new EventNotificationAlarmManager(EventInfoActivity.this);
                                manager.cancelEventNotificationAlarm(eventData);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventInfoActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fetchEventInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchEventInfo();
    }

    private void fetchEventInfo() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.open();
        eventData = dbHelper.getEvent(eventId);
        dbHelper.close();

        if (eventData == null) {
            finish();
            return;
        }

        nameText.setText(eventData.getName());
        descText.setText(eventData.getDesc());
        dateText.setText(dateFormat.format(eventData.getDate()));
    }

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_calendar;
    }
}
