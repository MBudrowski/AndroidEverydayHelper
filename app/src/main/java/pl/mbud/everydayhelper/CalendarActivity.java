package pl.mbud.everydayhelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.imanoweb.calendarview.DayDecorator;
import com.imanoweb.calendarview.DayView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.database.DatabaseHelper;

public class CalendarActivity extends BaseActivity {

    private CustomCalendarView calendarView;
    private DatabaseHelper dbHelper;

    private Button addEventButton, eventListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CustomCalendarView) findViewById(R.id.activity_calendar_view);
        addEventButton = (Button) findViewById(R.id.calendar_add_event);
        eventListButton = (Button) findViewById(R.id.calendar_event_list);
        dbHelper = new DatabaseHelper(this);

        final Calendar calendar = Calendar.getInstance();

        calendarView.setFirstDayOfWeek(getResources().getIntArray(R.array.weekDaysValues)[0]);
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                Calendar inner = Calendar.getInstance();
                for (DayDecorator dd : calendarView.getDecorators()) {
                    inner.setTime(((EventDecorator) dd).getData().getDate());
                    inner.set(Calendar.HOUR_OF_DAY, 0);
                    inner.set(Calendar.MINUTE, 0);
                    inner.set(Calendar.SECOND, 0);
                    inner.set(Calendar.MILLISECOND, 0);
                    if (inner.getTime().getTime() == c.getTime().getTime()) {
                        Intent intent = new Intent(CalendarActivity.this, EventListActivity.class);
                        intent.putExtra(EventListActivity.EXTRA_EVENT_DAY, c.get(Calendar.DAY_OF_MONTH));
                        intent.putExtra(EventListActivity.EXTRA_EVENT_MONTH, c.get(Calendar.MONTH));
                        intent.putExtra(EventListActivity.EXTRA_EVENT_YEAR, c.get(Calendar.YEAR));
                        startActivity(intent);
                        return;
                    }
                }
                Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
                intent.putExtra(AddEventActivity.EXTRA_EVENT_DAY, c.get(Calendar.DAY_OF_MONTH));
                intent.putExtra(AddEventActivity.EXTRA_EVENT_MONTH, c.get(Calendar.MONTH));
                intent.putExtra(AddEventActivity.EXTRA_EVENT_YEAR, c.get(Calendar.YEAR));
                startActivity(intent);
            }

            @Override
            public void onMonthChanged(Date date) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
            }
        });
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });
        eventListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper.open();
        List<EventData> events = dbHelper.getEvents();
        dbHelper.close();

        List<DayDecorator> decorators = new ArrayList<>();
        for (EventData data : events) {
            decorators.add(new EventDecorator(data));
        }
        calendarView.setDecorators(decorators);

        calendarView.markDayAsSelectedDay(new Date());
        calendarView.refreshCalendar(Calendar.getInstance());
    }

    private class EventDecorator implements DayDecorator {

        private EventData data;

        private EventDecorator(EventData data) {
            this.data = data;
        }

        public EventData getData() {
            return data;
        }

        @Override
        public void decorate(DayView dayView) {
            if (dayView.isSelected()) {
                dayView.setBackgroundColor(Color.WHITE);
            }
            Calendar c = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c.setTime(data.getDate());
            c2.setTime(dayView.getDate());
            if (c.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) && c.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
                dayView.setBackgroundColor(getResources().getColor(R.color.colorCalendarEvent));
            }
        }
    }

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_calendar;
    }
}
