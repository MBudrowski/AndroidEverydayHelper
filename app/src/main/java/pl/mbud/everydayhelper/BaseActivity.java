package pl.mbud.everydayhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Maciek on 30.12.2016.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_general, menu);

        Integer itemId = getMenuItemId();
        if (itemId != null) {
            menu.findItem(itemId).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_general_settings:
                Intent is = new Intent(this, SettingsActivity.class);
                startActivity(is);
                return true;
            case R.id.menu_general_weather:
                Intent iw = new Intent(this, WeatherActivity.class);
                startActivity(iw);
                return true;
            case R.id.menu_general_alarm:
                Intent ia = new Intent(this, AlarmsActivity.class);
                startActivity(ia);
                break;
            case R.id.menu_general_calendar:
                Intent ic = new Intent(this, CalendarActivity.class);
                startActivity(ic);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected Integer getMenuItemId() {
        return null;
    }
}
