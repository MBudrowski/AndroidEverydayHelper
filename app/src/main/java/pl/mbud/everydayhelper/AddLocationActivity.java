package pl.mbud.everydayhelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import pl.mbud.everydayhelper.fragments.AddLocationFragment;
import pl.mbud.everydayhelper.fragments.WeatherInfoFragment;

/**
 * Created by Maciek on 31.12.2016.
 */

public class AddLocationActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "ADD-LOCATION-ACT";

    private AddLocationFragment fragment;
    private Button addButton;
    private LocationManager locationManager;
    private List<LocationListener> listeners = new LinkedList<>();
    private Object lock = new Object();

    private class ConcreteLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            synchronized (lock) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Location found at: " + location.getLatitude() + ", " + location.getLongitude());
                if (!listeners.contains(this)) {
                    return;
                }
                Intent intent = new Intent(AddLocationActivity.this, WeatherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_LON, location.getLongitude());
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_LAT, location.getLatitude());
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_NAME, fragment.getEditText().getText().toString());
                startActivity(intent);
                cancelAllListeners();
                finish();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        addButton = (Button) findViewById(R.id.activity_add_location_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationName = fragment.getEditText().getText().toString();
                if (locationName != null && !locationName.isEmpty()) {
                    if (fragment.getCheckBox().isEnabled() && fragment.getCheckBox().isChecked()) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        List<String> list = new LinkedList<String>();
                        list.add(LocationManager.GPS_PROVIDER);
                        list.add(LocationManager.NETWORK_PROVIDER);
                        if (list.isEmpty()) {
                            Toast.makeText(getBaseContext(), getString(R.string.couldnt_find_the_location), Toast.LENGTH_SHORT).show();
                        }
                        for (String p : list) {
                            if (ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            Location location = locationManager.getLastKnownLocation(p);
                            LocationListener listener = new ConcreteLocationListener();

                            listeners.add(listener);
                            locationManager.requestLocationUpdates(p, 0, 0, listener);

                            if (location != null)
                                listener.onLocationChanged(location);

                        }
                    } else {
                        Intent intent = new Intent(AddLocationActivity.this, WeatherActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_NAME, locationName);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    fragment.getEditText().requestFocus();
                    fragment.getEditText().setError(getString(R.string.field_required));
                }
            }
        });

        fragment = new AddLocationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_add_location_fragment, fragment);
        transaction.commit();

    }

    private void cancelAllListeners() {
        synchronized (listeners) {
            for (LocationListener l : listeners) {
                locationManager.removeUpdates(l);
            }
            listeners.clear();
        }
    }
}
