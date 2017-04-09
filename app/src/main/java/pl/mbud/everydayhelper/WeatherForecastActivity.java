package pl.mbud.everydayhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import pl.mbud.everydayhelper.fragments.WeatherForecastFragment;
import pl.mbud.everydayhelper.fragments.WeatherInfoFragment;

/**
 * Created by Maciek on 30.12.2016.
 */

public class WeatherForecastActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        Fragment fragInfo = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putInt(WeatherInfoFragment.EXTRA_LOCATION_ID, getIntent().getIntExtra(WeatherInfoFragment.EXTRA_LOCATION_ID, 0));
        fragInfo.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_weather_forecast_fragment, fragInfo);
        transaction.commit();
    }

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_weather;
    }
}
