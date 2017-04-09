package pl.mbud.everydayhelper.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.listeners.EventListener;

/**
 * Created by Maciek on 12.12.2016.
 */

public class WeatherConnection {

    public static final String DEBUG_TAG = "WEATHER-CONNECTION";

    private static final String API_KEY = "d452f1622f6ddfe1dfa63681772b94ed";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String IMG_URL = "http://openweathermap.org/img/w/";

    private Context appContext;

    public WeatherConnection(Context context) {
        this.appContext = context;
    }

    private static class RequestData extends AsyncTask<String, Void, String> {

        private enum RequestType {
            SINGLE_WEATHER, FORECAST
        }

        private RequestType type;
        private Location location;
        private EventListener<List<WeatherData>> forecastReceivedListener;
        private EventListener<WeatherData> weatherDataReceivedListener;
        private boolean sync = false;

        protected RequestData(RequestType type, Location location) {
            this(type, location, false);
        }

        protected RequestData(RequestType type, Location location, boolean sync) {
            this.type = type;
            this.location = location;
            this.sync = sync;
        }

        public void setForecastReceivedListener(EventListener<List<WeatherData>> forecastReceivedListener) {
            this.forecastReceivedListener = forecastReceivedListener;
        }

        public void setWeatherDataReceivedListener(EventListener<WeatherData> weatherDataReceivedListener) {
            this.weatherDataReceivedListener = weatherDataReceivedListener;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting URL: " + strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    urlConnection.disconnect();
                    return sb.toString();
                }

                String s;
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((s = in.readLine()) != null) {
                        sb.append(s);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (sync) {
                return;
            }

            if (type == RequestType.FORECAST) {
                try {
                    JSONObject obj = new JSONObject(s);
                    if (location.getLocationId() == null) {
                        JSONObject city = obj.getJSONObject("city");
                        location.setLocationId(city.getInt("id"));
                        location.setCountryCode(city.getString("country"));
                        location.setLocationName(city.getString("name"));
                    }
                    JSONArray weatherList = obj.getJSONArray("list");
                    WeatherData data;
                    List<WeatherData> list = new LinkedList<>();
                    for (int i = 0; i < weatherList.length(); i++) {
                        data = new WeatherData(weatherList.getJSONObject(i));
                        data.setLocation(location);
                        list.add(data);
                    }
                    if (forecastReceivedListener != null) {
                        forecastReceivedListener.onEvent(list);
                    }
                    //EventBus.getDefault().post(list);
                } catch (JSONException e) {
                    if (forecastReceivedListener != null) {
                        forecastReceivedListener.onEvent(null);
                    }
                    Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
                }
            } else {
                try {
                    WeatherData data = new WeatherData(location, new JSONObject(s));
                    if (weatherDataReceivedListener != null) {
                        weatherDataReceivedListener.onEvent(data);
                    }
                } catch (JSONException e) {
                    if (weatherDataReceivedListener != null) {
                        weatherDataReceivedListener.onEvent(null);
                    }
                    Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    private static class RequestBitmap extends AsyncTask<String, Void, Bitmap> {

        private boolean sync = false;
        private EventListener<Bitmap> bitmapReceivedListener;

        public RequestBitmap() {

        }

        public RequestBitmap(boolean sync) {
            this.sync = sync;
        }

        public void setBitmapReceivedListener(EventListener<Bitmap> bitmapReceivedListener) {
            this.bitmapReceivedListener = bitmapReceivedListener;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap img = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(10000);
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting URL: " + strings[0]);
                urlConnection.connect();
                try {
                    InputStream is = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byte[] result = baos.toByteArray();
                    img = BitmapFactory.decodeByteArray(result, 0, result.length);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (sync) {
                return;
            }
            //EventBus.getDefault().post(bitmap);
            if (bitmapReceivedListener != null) {
                bitmapReceivedListener.onEvent(bitmap);
            }
        }
    }

    public void fetchWeatherDataForLocation(Location location, EventListener<WeatherData> listener) {
        ConnectivityManager connMgr = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            RequestData task = new RequestData(RequestData.RequestType.SINGLE_WEATHER, location);
            task.setWeatherDataReceivedListener(listener);
            if (location.getLocationId() != null) {
                task.execute(BASE_URL + "weather?id=" + location.getLocationId() + "&appid=" + API_KEY);
            } else {
                task.execute(BASE_URL + "weather?q=" + location.getCustomName().replaceAll("\\s+", "+") + "&appid=" + API_KEY);
            }
        }
        else {
            if (listener != null) {
                listener.onEvent(null);
            }
        }
    }

    public WeatherData fetchWeatherDataForLocationSync(Location location) {
        ConnectivityManager connMgr = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            RequestData task = new RequestData(RequestData.RequestType.SINGLE_WEATHER, location);
            if (location.getLocationId() != null) {
                task.execute(BASE_URL + "weather?id=" + location.getLocationId() + "&appid=" + API_KEY);
            } else if (location.getCoordinate() != null) {
                task.execute(BASE_URL + "weather?lat=" + location.getCoordinate().getLatitude() + "&lon=" + location.getCoordinate().getLongitude() + "&appid=" + API_KEY);
            } else {
                task.execute(BASE_URL + "weather?q=" + location.getCustomName().replaceAll("\\s+", "+") + "&appid=" + API_KEY);
            }
            try {
                return new WeatherData(location, new JSONObject(task.get()));
            } catch (JSONException | InterruptedException | ExecutionException e) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
            }
        }
        return null;
    }

    public void fetchForecastForLocation(Location location, EventListener<List<WeatherData>> listener) {
        ConnectivityManager connMgr = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            RequestData task = new RequestData(RequestData.RequestType.FORECAST, location);
            task.setForecastReceivedListener(listener);
            if (location.getLocationId() != null) {
                task.execute(BASE_URL + "forecast?id=" + location.getLocationId() + "&appid=" + API_KEY);
            } else {
                task.execute(BASE_URL + "forecast?q=" + location.getCustomName() + "&appid=" + API_KEY);
            }
        }
        else {
            if (listener != null) {
                listener.onEvent(null);
            }
        }
    }

    public List<WeatherData> fetchForecastForLocationSync(Location location) {
        ConnectivityManager connMgr = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            RequestData task = new RequestData(RequestData.RequestType.FORECAST, location, true);
            if (location.getLocationId() != null) {
                task.execute(BASE_URL + "forecast?id=" + location.getLocationId() + "&appid=" + API_KEY);
            } else {
                task.execute(BASE_URL + "forecast?q=" + location.getCustomName() + "&appid=" + API_KEY);
            }
            try {
                JSONObject obj = new JSONObject(task.get());
                if (location.getLocationId() == null) {
                    JSONObject city = obj.getJSONObject("city");
                    location.setLocationId(city.getInt("id"));
                    location.setCountryCode(city.getString("country"));
                    location.setLocationName(city.getString("name"));
                }
                JSONArray weatherList = obj.getJSONArray("list");
                WeatherData data;
                List<WeatherData> list = new LinkedList<>();
                for (int i = 0; i < weatherList.length(); i++) {
                    data = new WeatherData(weatherList.getJSONObject(i));
                    data.setLocation(location);
                    list.add(data);
                }
                return list;
            } catch (InterruptedException | JSONException | ExecutionException e) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
            }
        }
        return null;
    }

    public Bitmap fetchImageSync(String name) {
        ConnectivityManager connMgr = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            RequestBitmap task = new RequestBitmap(true);
            task.execute(IMG_URL + name + ".png");
            try {
                return task.get();
            } catch (InterruptedException e) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
            } catch (ExecutionException e) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, Log.getStackTraceString(e));
            }
        }
        return null;
    }

    public void fetchImage(String name, EventListener<Bitmap> listener) {
        ConnectivityManager connMgr = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            RequestBitmap task = new RequestBitmap();
            task.setBitmapReceivedListener(listener);
            task.execute(IMG_URL + name + ".png");
        }
        else {
            if (listener != null) {
                listener.onEvent(null);
            }
        }
    }
}
