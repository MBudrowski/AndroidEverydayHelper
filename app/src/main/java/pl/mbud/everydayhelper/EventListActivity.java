package pl.mbud.everydayhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pl.mbud.everydayhelper.adapters.AlarmListAdapter;
import pl.mbud.everydayhelper.adapters.EventListAdapter;
import pl.mbud.everydayhelper.alarm.EventNotificationAlarmManager;
import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;

public class EventListActivity extends BaseActivity implements RecyclerViewItemClickListener<EventData> {

    public static final String EXTRA_EVENT_DAY = "dayNum";
    public static final String EXTRA_EVENT_MONTH = "monthNum";
    public static final String EXTRA_EVENT_YEAR = "yearNum";

    private List<EventData> eventList1;
    private List<EventData> eventList2;
    private EventListAdapter adapter1;
    private EventListAdapter adapter2;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private View pastBlock;

    private TextView noneView1, noneView2, firstName;

    private Button addButton, calendarButton;
    private DatabaseHelper dbHelper;

    private int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        day = getIntent().getIntExtra(EXTRA_EVENT_DAY, -1);
        month = getIntent().getIntExtra(EXTRA_EVENT_MONTH, -1);
        year = getIntent().getIntExtra(EXTRA_EVENT_YEAR, -1);

        recyclerView1 = (RecyclerView) findViewById(R.id.activity_event_list_list1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        eventList1 = new ArrayList<>();
        adapter1 = new EventListAdapter(eventList1, this);
        recyclerView1.setAdapter(adapter1);
        adapter1.addOnItemClickListener(this);

        recyclerView2 = (RecyclerView) findViewById(R.id.activity_event_list_list2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        eventList2 = new ArrayList<>();
        adapter2 = new EventListAdapter(eventList2, this);
        recyclerView2.setAdapter(adapter2);
        adapter2.addOnItemClickListener(this);

        addButton = (Button) findViewById(R.id.activity_event_list_add_event);
        calendarButton = (Button) findViewById(R.id.activity_event_list_calendar);
        noneView1 = (TextView) findViewById(R.id.activity_event_list_none1);
        noneView2 = (TextView) findViewById(R.id.activity_event_list_none2);
        firstName = (TextView) findViewById(R.id.activity_event_list_name1);
        pastBlock = findViewById(R.id.activity_event_list_past_block);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventListActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventListActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dbHelper.open();
        if (day != -1 && month != -1 && year != -1) {
            eventList1.clear();
            eventList1.addAll(dbHelper.getEventsInDay(day, month, year));
            adapter1.notifyDataSetChanged();
            if (eventList1.isEmpty()) {
                noneView1.setVisibility(View.VISIBLE);
                recyclerView1.setVisibility(View.INVISIBLE);
            } else {
                noneView1.setVisibility(View.INVISIBLE);
                recyclerView1.setVisibility(View.VISIBLE);
            }
            pastBlock.setVisibility(View.INVISIBLE);
            firstName.setText(getString(R.string.event_list_info));
        }
        else {
            eventList1.clear();
            eventList1.addAll(dbHelper.getEventsFuture());
            adapter1.notifyDataSetChanged();
            if (eventList1.isEmpty()) {
                noneView1.setVisibility(View.VISIBLE);
                recyclerView1.setVisibility(View.INVISIBLE);
            } else {
                noneView1.setVisibility(View.INVISIBLE);
                recyclerView1.setVisibility(View.VISIBLE);
            }
            eventList2.clear();
            eventList2.addAll(dbHelper.getEventsPast());
            adapter2.notifyDataSetChanged();
            if (eventList2.isEmpty()) {
                noneView2.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.INVISIBLE);
            } else {
                noneView2.setVisibility(View.INVISIBLE);
                recyclerView2.setVisibility(View.VISIBLE);
            }
        }
        dbHelper.close();

    }

    @Override
    public void onItemClicked(View view, EventData item, int position) {
        Intent intent = new Intent(EventListActivity.this, EventInfoActivity.class);
        intent.putExtra(EventInfoActivity.EXTRA_EVENT_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClicked(View view, final EventData data, int position) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.menu_event_list, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_event_item_edit:
                        Intent intent = new Intent(EventListActivity.this, AddEventActivity.class);
                        intent.putExtra(AddEventActivity.EXTRA_EVENT_ID, data.getId());
                        startActivity(intent);
                        return true;
                    case R.id.menu_event_item_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(EventListActivity.this);
                        builder.setTitle(getString(R.string.confirm_deletion));
                        builder.setMessage(getString(R.string.are_you_sure_delete_event))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseHelper dbHelper = new DatabaseHelper(EventListActivity.this);
                                        dbHelper.open();
                                        dbHelper.deleteEvent(data.getId());
                                        dbHelper.close();
                                        dialogInterface.dismiss();
                                        EventNotificationAlarmManager manager = new EventNotificationAlarmManager(EventListActivity.this);
                                        manager.cancelEventNotificationAlarm(data);
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

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_calendar;
    }
}
