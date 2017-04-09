package pl.mbud.everydayhelper.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.LocationListActivity;
import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.data.Coordinate;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.util.NumberFormatter;
import pl.mbud.everydayhelper.util.WeatherDescUtil;
import pl.mbud.everydayhelper.util.WindDirectionUtil;
import pl.mbud.everydayhelper.weather.TemperatureScale;
import pl.mbud.everydayhelper.weather.WeatherData;

public class WeatherInfoFragment extends Fragment {
    public static final String EXTRA_LOCATION_NAME = "Location";
    public static final String EXTRA_LOCATION_ID = "LocationID";
    public static final String EXTRA_LOCATION_LAT = "latitude";
    public static final String EXTRA_LOCATION_LON = "longitude";
    public static final String EXTRA_WEATHER_ID = "WeatherID";

    private Location location;
    private WeatherData data;
    private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_weather_info,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String locationString = getArguments().getString(EXTRA_LOCATION_NAME, "");
        int locationId = getArguments().getInt(EXTRA_LOCATION_ID, 0);
        double lon = getArguments().getDouble(EXTRA_LOCATION_LON, -999);
        double lat = getArguments().getDouble(EXTRA_LOCATION_LAT, -999);
        int weatherId = getArguments().getInt(EXTRA_WEATHER_ID, 0);

        DatabaseHelper dbHelper = BaseApplication.getInstance().getDbHelper();
        dbHelper.open();

        if (weatherId != 0) {
            data = dbHelper.getWeatherData(weatherId);

            if (data != null) {
                location = data.getLocation();
            }
        } else {
            if (locationId != 0) {
                location = dbHelper.getLocation(locationId);
                if (location == null) {
                    location = new Location(locationId);
                }
            } else if (!locationString.isEmpty()) {
                location = new Location(locationString);
                if (lon != -999 && lat != -999) {
                    location.setCoordinate(new Coordinate(lon, lat));
                }
            } else {
                Intent intent = new Intent(getActivity(), LocationListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                return;
            }

            data = dbHelper.getWeatherDataForLocation(location);
        }
        if (data == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(getString(R.string.error));
            alertDialog.setMessage(getString(R.string.download_error_location_name));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().onBackPressed();
                        }
                    });
            alertDialog.show();
            return;
        }
        TextView customName = (TextView) getView().findViewById(R.id.value_custom_name);
        customName.setText(data.getLocation().getCustomName());
        TextView location = (TextView) getView().findViewById(R.id.value_location);
        location.setText(data.getLocationName() + ", " + data.getCountryCode());
        TextView weather = (TextView) getView().findViewById(R.id.value_weather);
        weather.setText(WeatherDescUtil.getWeatherDescriptionFromId(data.getWeatherId(), getActivity()));
        TextView temperature = (TextView) getView().findViewById(R.id.value_temperature);
        TemperatureScale scale = TemperatureScale.valueOf(BaseApplication.getInstance().getPreferences().getString("temperatureScale", "CELCIUS"));
        temperature.setText(NumberFormatter.getTemperatureFormatted(data.getTemperature(scale)) + scale.getSuffix(getActivity()));
        TextView pressure = (TextView) getView().findViewById(R.id.value_pressure);
        pressure.setText(data.getPressure() + " hPa");
        TextView humidity = (TextView) getView().findViewById(R.id.value_humidity);
        humidity.setText(data.getHumidity() + "%");
        TextView wind = (TextView) getView().findViewById(R.id.value_wind);
        wind.setText(data.getWindSpeed() + " m/s (" + WindDirectionUtil.getDirectionString(data.getWindAngle(), getActivity()) + ")");
        TextView clouds = (TextView) getView().findViewById(R.id.value_clouds);
        clouds.setText(data.getCloudLevel() + "%");
        TextView rain = (TextView) getView().findViewById(R.id.value_rain);
        if (data.getRainLevel() == null) {
            rain.setText("0 mm");
        }
        else {
            rain.setText(data.getRainLevel() + " mm");
        }
        TextView snow = (TextView) getView().findViewById(R.id.value_snow);
        if (data.getSnowLevel() == null) {
            snow.setText("0 mm");
        }
        else {
            snow.setText(data.getSnowLevel() + " mm");
        }
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        TextView sunrise = (TextView) getView().findViewById(R.id.value_sunrise);
        if (data.getSunrise() == null || data.getSunrise().getTime() == 0l) {
            sunrise.setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.label_sunrise).setVisibility(View.INVISIBLE);
        }
        else {
            sunrise.setText(dateFormat.format(data.getSunrise()));
        }
        TextView sunset = (TextView) getView().findViewById(R.id.value_sunset);
        if (data.getSunset() == null || data.getSunset().getTime() == 0l) {
            sunset.setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.label_sunset).setVisibility(View.INVISIBLE);
        }
        else {
            sunset.setText(dateFormat.format(data.getSunset()));
        }
        TextView date = (TextView) getView().findViewById(R.id.value_date);
        date.setText(dateTimeFormat.format(data.getForecastDate()));
        ImageView image = (ImageView) getView().findViewById(R.id.weatherIcon);
        image.setImageBitmap(data.getIcon().getBitmap());
        dbHelper.close();

        getView().setVisibility(View.VISIBLE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public Location getLocation() {
        return location;
    }

    public WeatherData getWeatherData() {
        return data;
    }
}