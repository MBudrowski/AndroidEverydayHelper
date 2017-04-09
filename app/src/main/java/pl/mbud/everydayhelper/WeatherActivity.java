package pl.mbud.everydayhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.events.WeatherUpdateEvent;
import pl.mbud.everydayhelper.fragments.WeatherInfoFragment;

public class WeatherActivity extends BaseActivity {

    public static final String DEBUG_TAG = "WEATHER-ACT";

    private Button otherLocationsButton, forecastButton, refreshButton;
    private Location location;
    private WeatherInfoFragment fragment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        otherLocationsButton = (Button) findViewById(R.id.weather_other_locations);
        forecastButton = (Button) findViewById(R.id.weather_forecast);
        refreshButton = (Button) findViewById(R.id.weather_refresh_data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupFragment(false);
    }

    private void setupFragment(boolean defaultWeather) {
        otherLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeatherActivity.this, LocationListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(WeatherActivity.this, getString(R.string.updating_data), getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                Intent intent = new Intent(WeatherActivity.this, WeatherService.class);
                startService(intent);
            }
        });

        Bundle bundle = new Bundle();
        if (fragment != null) {
            bundle.putInt(WeatherInfoFragment.EXTRA_LOCATION_ID, fragment.getLocation().getLocationId());
            bundle.putString(WeatherInfoFragment.EXTRA_LOCATION_NAME, fragment.getLocation().getCustomName());
            if (!defaultWeather) {
                bundle.putInt(WeatherInfoFragment.EXTRA_WEATHER_ID, fragment.getWeatherData().getId());
            }
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                bundle.putInt(WeatherInfoFragment.EXTRA_LOCATION_ID, extras.getInt(WeatherInfoFragment.EXTRA_LOCATION_ID, 0));
                bundle.putString(WeatherInfoFragment.EXTRA_LOCATION_NAME, extras.getString(WeatherInfoFragment.EXTRA_LOCATION_NAME, ""));
                bundle.putDouble(WeatherInfoFragment.EXTRA_LOCATION_LON, extras.getDouble(WeatherInfoFragment.EXTRA_LOCATION_LON, -999));
                bundle.putDouble(WeatherInfoFragment.EXTRA_LOCATION_LAT, extras.getDouble(WeatherInfoFragment.EXTRA_LOCATION_LAT, -999));
                bundle.putInt(WeatherInfoFragment.EXTRA_WEATHER_ID, extras.getInt(WeatherInfoFragment.EXTRA_WEATHER_ID, 0));
            } else {
                DatabaseHelper dbHelper = BaseApplication.getInstance().getDbHelper();
                dbHelper.open();
                location = dbHelper.getDefaultLocation();
                dbHelper.close();
                if (location == null) {
                    Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "No default location found. Opening location list activity...");
                    Intent intent = new Intent(this, LocationListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    bundle.putInt(WeatherInfoFragment.EXTRA_LOCATION_ID, location.getLocationId());
                }
            }
        }

        forecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeatherActivity.this, WeatherForecastActivity.class);
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_ID, fragment.getLocation().getLocationId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        fragment = new WeatherInfoFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_weather_fragment, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_weather;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.activity_weather_fragment, fragment);
        transaction.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWeatherUpdateCompleted(WeatherUpdateEvent event) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (event.isSuccess()) {
            setupFragment(true);
        } else {
            setupFragment(true);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.error));
            alertDialog.setMessage(getString(R.string.download_error));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

}
