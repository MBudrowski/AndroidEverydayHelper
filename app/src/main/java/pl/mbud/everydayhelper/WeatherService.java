package pl.mbud.everydayhelper;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import pl.mbud.everydayhelper.database.DatabaseHelper;

public class WeatherService extends Service {
    public static final String DEBUG_TAG = "UPDATE-SERVICE";

    private class UpdateTask extends AsyncTask<Intent, Void, Void> {

        @Override
        protected Void doInBackground(Intent... intents) {
            DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
            dbHelper.open();
            dbHelper.updateAllDataAsync();
            return null;
        }
    }

    public WeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Updating weather data...");
        new UpdateTask().execute(intent);
        return START_NOT_STICKY;
    }
}
