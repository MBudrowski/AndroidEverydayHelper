package pl.mbud.everydayhelper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.AlarmShutOffMethod;
import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.data.WeatherIcon;
import pl.mbud.everydayhelper.events.WeatherUpdateEvent;
import pl.mbud.everydayhelper.listeners.EventListener;
import pl.mbud.everydayhelper.util.BitmapUtility;
import pl.mbud.everydayhelper.weather.WeatherConnection;
import pl.mbud.everydayhelper.weather.WeatherData;

/**
 * Created by Maciek on 31.12.2016.
 */

public class DatabaseHelper {
    private static final String DEBUG_TAG = "DB-HELPER";

    private static final int DB_VERSION = 10;
    private static final String DB_NAME = "everydayHelper.db";

    private static final String DB_LOCATION_TABLE = "location";
    public static final String LOC_KEY_ID = "_id";
    public static final String LOC_ID_OPTIONS = "INTEGER PRIMARY KEY";
    public static final int LOC_ID_COLUMN = 0;
    public static final String LOC_KEY_NAME = "custom_name";
    public static final String LOC_NAME_OPTIONS = "TEXT NOT NULL";
    public static final int LOC_NAME_COLUMN = 1;
    public static final String LOC_KEY_LOCATION_NAME = "name";
    public static final String LOC_LOCATION_NAME_OPTIONS = "TEXT NOT NULL";
    public static final int LOC_LOCATION_NAME_COLUMN = 2;
    public static final String LOC_KEY_COUNTRY = "country";
    public static final String LOC_COUNTRY_OPTIONS = "TEXT NOT NULL";
    public static final int LOC_COUNTRY_COLUMN = 3;
    public static final String LOC_KEY_DEFAULT = "is_default";
    public static final String LOC_DEFAULT_OPTIONS = "BOOLEAN DEFAULT 0";
    public static final int LOC_DEFAULT_COLUMN = 4;

    private static final String DB_WEATHER_TABLE = "weather";
    public static final String WEA_KEY_ID = "_id";
    public static final String WEA_ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int WEA_ID_COLUMN = 0;
    public static final String WEA_KEY_LOCATION = "location";
    public static final String WEA_LOCATION_OPTIONS = "INTEGER NOT NULL";
    public static final int WEA_LOCATION_COLUMN = 1;
    public static final String WEA_KEY_TEMP = "temp";
    public static final String WEA_TEMP_OPTIONS = "DOUBLE";
    public static final int WEA_TEMP_COLUMN = 2;
    public static final String WEA_KEY_PRESSURE = "pressure";
    public static final String WEA_PRESSURE_OPTIONS = "DOUBLE";
    public static final int WEA_PRESSURE_COLUMN = 3;
    public static final String WEA_KEY_HUMIDITY = "humidity";
    public static final String WEA_HUMIDITY_OPTIONS = "DOUBLE";
    public static final int WEA_HUMIDITY_COLUMN = 4;
    public static final String WEA_KEY_DATE = "date";
    public static final String WEA_DATE_OPTIONS = "INTEGER";
    public static final int WEA_DATE_COLUMN = 5;
    public static final String WEA_KEY_WEATHER_ID = "weather_id";
    public static final String WEA_WEATHER_ID_OPTIONS = "INTEGER";
    public static final int WEA_WEATHER_ID_COLUMN = 6;
    public static final String WEA_KEY_ICON = "weather_icon";
    public static final String WEA_ICON_OPTIONS = "TEXT";
    public static final int WEA_ICON_COLUMN = 7;
    public static final String WEA_KEY_CLOUDS = "clouds";
    public static final String WEA_CLOUDS_OPTIONS = "INTEGER";
    public static final int WEA_CLOUDS_COLUMN = 8;
    public static final String WEA_KEY_WIND_SPEED = "wind_speed";
    public static final String WEA_WIND_SPEED_OPTIONS = "DOUBLE";
    public static final int WEA_WIND_SPEED_COLUMN = 9;
    public static final String WEA_KEY_WIND_DEG = "wind_deg";
    public static final String WEA_WIND_DEG_OPTIONS = "DOUBLE";
    public static final int WEA_WIND_DEG_COLUMN = 10;
    public static final String WEA_KEY_RAIN = "rain";
    public static final String WEA_RAIN_OPTIONS = "DOUBLE";
    public static final int WEA_RAIN_COLUMN = 11;
    public static final String WEA_KEY_SNOW = "snow";
    public static final String WEA_SNOW_OPTIONS = "DOUBLE";
    public static final int WEA_SNOW_COLUMN = 12;
    public static final String WEA_KEY_SUNRISE = "sunrise";
    public static final String WEA_SUNRISE_OPTIONS = "INTEGER";
    public static final int WEA_SUNRISE_COLUMN = 13;
    public static final String WEA_KEY_SUNSET = "sunset";
    public static final String WEA_SUNSET_OPTIONS = "INTEGER";
    public static final int WEA_SUNSET_COLUMN = 14;

    private static final String DB_ICON_TABLE = "icon";
    public static final String ICON_KEY_NAME = "iconName";
    public static final String ICON_NAME_OPTIONS = "TEXT PRIMARY KEY NOT NULL";
    public static final int ICON_NAME_COLUMN = 0;
    public static final String ICON_KEY_BITMAP = "bitmap";
    public static final String ICON_BITMAP_OPTIONS = "BLOB";
    public static final int ICON_BITMAP_COLUMN = 1;

    private static final String DB_ALARM_TABLE = "alarm";
    public static final String ALARM_KEY_ID = "_id";
    public static final String ALARM_ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ALARM_ID_COLUMN = 0;
    public static final String ALARM_KEY_NAME = "name";
    public static final String ALARM_NAME_OPTIONS = "TEXT";
    public static final int ALARM_NAME_COLUMN = 1;
    public static final String ALARM_KEY_TIME = "time";
    public static final String ALARM_TIME_OPTIONS = "INTEGER";
    public static final int ALARM_TIME_COLUMN = 2;
    public static final String ALARM_KEY_RINGTONE = "ringtone";
    public static final String ALARM_RINGTONE_OPTIONS = "TEXT";
    public static final int ALARM_RINGTONE_COLUMN = 3;
    public static final String ALARM_KEY_VOLUME = "ringtone_volume";
    public static final String ALARM_VOLUME_OPTIONS = "INTEGER";
    public static final int ALARM_VOLUME_COLUMN = 4;
    public static final String ALARM_KEY_REPEAT = "repeat";
    public static final String ALARM_REPEAT_OPTIONS = "INTEGER";
    public static final int ALARM_REPEAT_COLUMN = 5;
    public static final String ALARM_KEY_ENABLED = "enabled";
    public static final String ALARM_ENABLED_OPTIONS = "BOOLEAN";
    public static final int ALARM_ENABLED_COLUMN = 6;
    public static final String ALARM_KEY_SHUT_OFF = "shut_off_method";
    public static final String ALARM_SHUT_OFF_OPTIONS = "INT";
    public static final int ALARM_SHUT_OFF_COLUMN = 7;

    private static final String DB_EVE_TABLE = "event";
    public static final String EVE_KEY_ID = "_id";
    public static final String EVE_ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int EVE_ID_COLUMN = 0;
    public static final String EVE_KEY_NAME = "name";
    public static final String EVE_NAME_OPTIONS = "TEXT";
    public static final int EVE_NAME_COLUMN = 1;
    public static final String EVE_KEY_DESC = "desc";
    public static final String EVE_DESC_OPTIONS = "TEXT";
    public static final int EVE_DESC_COLUMN = 2;
    public static final String EVE_KEY_DATE = "date";
    public static final String EVE_DATE_OPTIONS = "INT";
    public static final int EVE_DATE_COLUMN = 3;
    public static final String EVE_KEY_NOTIFY = "time_before_notification";
    public static final String EVE_NOTIFY_OPTIONS = "INT";
    public static final int EVE_NOTIFY_COLUMN = 4;

    private static final String DB_CREATE_LOCATION_TABLE =
            "CREATE TABLE " + DB_LOCATION_TABLE + "( " +
                    LOC_KEY_ID + " " + LOC_ID_OPTIONS + ", " +
                    LOC_KEY_DEFAULT + " " + LOC_DEFAULT_OPTIONS + ", " +
                    LOC_KEY_NAME + " " + LOC_NAME_OPTIONS + ", " +
                    LOC_KEY_LOCATION_NAME + " " + LOC_LOCATION_NAME_OPTIONS + ", " +
                    LOC_KEY_COUNTRY + " " + LOC_COUNTRY_OPTIONS +
                    ");";
    private static final String DROP_LOCATION_TABLE =
            "DROP TABLE IF EXISTS " + DB_LOCATION_TABLE;

    private static final String DB_CREATE_WEATHER_TABLE =
            "CREATE TABLE " + DB_WEATHER_TABLE + "( " +
                    WEA_KEY_ID + " " + WEA_ID_OPTIONS + ", " +
                    WEA_KEY_LOCATION + " " + WEA_LOCATION_OPTIONS + ", " +
                    WEA_KEY_TEMP + " " + WEA_TEMP_OPTIONS + ", " +
                    WEA_KEY_PRESSURE + " " + WEA_PRESSURE_OPTIONS + ", " +
                    WEA_KEY_HUMIDITY + " " + WEA_HUMIDITY_OPTIONS + ", " +
                    WEA_KEY_DATE + " " + WEA_DATE_OPTIONS + ", " +
                    WEA_KEY_WEATHER_ID + " " + WEA_WEATHER_ID_OPTIONS + ", " +
                    WEA_KEY_ICON + " " + WEA_ICON_OPTIONS + ", " +
                    WEA_KEY_CLOUDS + " " + WEA_CLOUDS_OPTIONS + ", " +
                    WEA_KEY_WIND_SPEED + " " + WEA_WIND_SPEED_OPTIONS + ", " +
                    WEA_KEY_WIND_DEG + " " + WEA_WIND_DEG_OPTIONS + ", " +
                    WEA_KEY_RAIN + " " + WEA_RAIN_OPTIONS + ", " +
                    WEA_KEY_SNOW + " " + WEA_SNOW_OPTIONS + ", " +
                    WEA_KEY_SUNRISE + " " + WEA_SUNRISE_OPTIONS + ", " +
                    WEA_KEY_SUNSET + " " + WEA_SUNSET_OPTIONS +
                    ");";
    private static final String DROP_WEATHER_TABLE =
            "DROP TABLE IF EXISTS " + DB_WEATHER_TABLE;

    private static final String DB_CREATE_ICON_TABLE =
            "CREATE TABLE " + DB_ICON_TABLE + "( " +
                    ICON_KEY_NAME + " " + ICON_NAME_OPTIONS + ", " +
                    ICON_KEY_BITMAP + " " + ICON_BITMAP_OPTIONS +
                    ");";
    private static final String DROP_ICON_TABLE =
            "DROP TABLE IF EXISTS " + DB_ICON_TABLE;

    private static final String DB_CREATE_ALARM_TABLE =
            "CREATE TABLE " + DB_ALARM_TABLE + "( " +
                    ALARM_KEY_ID + " " + ALARM_ID_OPTIONS + ", " +
                    ALARM_KEY_NAME + " " + ALARM_NAME_OPTIONS + ", " +
                    ALARM_KEY_TIME + " " + ALARM_TIME_OPTIONS + ", " +
                    ALARM_KEY_RINGTONE + " " + ALARM_RINGTONE_OPTIONS + ", " +
                    ALARM_KEY_VOLUME + " " + ALARM_VOLUME_OPTIONS + ", " +
                    ALARM_KEY_REPEAT + " " + ALARM_REPEAT_OPTIONS + ", " +
                    ALARM_KEY_ENABLED + " " + ALARM_ENABLED_OPTIONS + ", " +
                    ALARM_KEY_SHUT_OFF + " " + ALARM_SHUT_OFF_OPTIONS +
                    ");";
    private static final String DROP_ALARM_TABLE =
            "DROP TABLE IF EXISTS " + DB_ALARM_TABLE;

    private static final String DB_CREATE_EVENT_TABLE =
            "CREATE TABLE " + DB_EVE_TABLE + "( " +
                    EVE_KEY_ID + " " + EVE_ID_OPTIONS + ", " +
                    EVE_KEY_NAME + " " + EVE_NAME_OPTIONS + ", " +
                    EVE_KEY_DESC + " " + EVE_DESC_OPTIONS + ", " +
                    EVE_KEY_DATE + " " + EVE_DATE_OPTIONS + ", " +
                    EVE_KEY_NOTIFY + " " + EVE_NOTIFY_OPTIONS +
                    ");";
    private static final String DROP_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + DB_EVE_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DBHelper dbHelper;
    private WeatherConnection connection;

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_LOCATION_TABLE);
            db.execSQL(DB_CREATE_WEATHER_TABLE);
            db.execSQL(DB_CREATE_ICON_TABLE);
            db.execSQL(DB_CREATE_ALARM_TABLE);
            db.execSQL(DB_CREATE_EVENT_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_LOCATION_TABLE + " ver." + DB_VERSION + " created");
            Log.d(DEBUG_TAG, "Table " + DB_WEATHER_TABLE + " ver." + DB_VERSION + " created");
            Log.d(DEBUG_TAG, "Table " + DB_ICON_TABLE + " ver." + DB_VERSION + " created");
            Log.d(DEBUG_TAG, "Table " + DB_ALARM_TABLE + " ver." + DB_VERSION + " created");
            Log.d(DEBUG_TAG, "Table " + DB_EVE_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_LOCATION_TABLE);
            db.execSQL(DROP_WEATHER_TABLE);
            db.execSQL(DROP_ICON_TABLE);
            db.execSQL(DROP_ALARM_TABLE);
            db.execSQL(DROP_EVENT_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_LOCATION_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "Table " + DB_WEATHER_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "Table " + DB_ICON_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "Table " + DB_ALARM_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "Table " + DB_EVE_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public DatabaseHelper(Context context) {
        this.context = context;
        this.connection = new WeatherConnection(context);
    }

    public DatabaseHelper open() {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(LOC_KEY_ID, location.getLocationId());
        values.put(LOC_KEY_NAME, location.getCustomName());
        values.put(LOC_KEY_LOCATION_NAME, location.getLocationName());
        values.put(LOC_KEY_COUNTRY, location.getCountryCode());
        return db.insert(DB_LOCATION_TABLE, null, values);
    }

    public boolean updateLocation(Location location) {
        return updateLocation(location.getLocationId(), location.getCustomName(), location.isDefaultLocation());
    }

    public boolean updateLocation(long id, String customName, boolean isDefault) {
        String where = LOC_KEY_ID + "=" + id;
        ContentValues values = new ContentValues();
        values.put(LOC_KEY_NAME, customName);
        values.put(LOC_KEY_DEFAULT, isDefault ? 1 : 0);
        return db.update(DB_LOCATION_TABLE, values, where, null) > 0;
    }

    public boolean deleteLocation(Location location) {
        return deleteLocation(location.getLocationId());
    }

    public boolean deleteLocation(long id) {
        deleteAllWeatherForLocation(id);
        String where = LOC_KEY_ID + "=" + id;
        return db.delete(DB_LOCATION_TABLE, where, null) > 0;
    }

    public List<Location> getAllLocations() {
        List<Location> list = new ArrayList<>();

        Cursor res = db.rawQuery("select * from " + DB_LOCATION_TABLE + " order by " + LOC_KEY_NAME, null);
        res.moveToFirst();

        Location tmp;
        while (res.isAfterLast() == false) {
            tmp = new Location();
            tmp.setLocationId(res.getInt(res.getColumnIndex(LOC_KEY_ID)));
            tmp.setCustomName(res.getString(res.getColumnIndex(LOC_KEY_NAME)));
            tmp.setLocationName(res.getString(res.getColumnIndex(LOC_KEY_LOCATION_NAME)));
            tmp.setCountryCode(res.getString(res.getColumnIndex(LOC_KEY_COUNTRY)));
            if (res.getInt(res.getColumnIndex(LOC_KEY_DEFAULT)) != 0) {
                tmp.setDefault(true);
            }
            list.add(tmp);
            res.moveToNext();
        }
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requested all locations. Found " + list.size());
        return list;
    }

    public Location getLocation(int id) {
        Location location = null;

        Cursor res = db.rawQuery("select * from " + DB_LOCATION_TABLE + " where " + LOC_KEY_ID + "=" + id + "", null);
        if (res.getCount() > 0) {
            res.moveToFirst();
            location = new Location(id);
            location.setCustomName(res.getString(res.getColumnIndex(LOC_KEY_NAME)));
            location.setLocationName(res.getString(res.getColumnIndex(LOC_KEY_LOCATION_NAME)));
            location.setCountryCode(res.getString(res.getColumnIndex(LOC_KEY_COUNTRY)));
            if (res.getInt(res.getColumnIndex(LOC_KEY_DEFAULT)) != 0) {
                location.setDefault(true);
            }
        }
        res.close();

        return location;
    }

    public boolean setDefaultLocation(Location location) {
        Location defaultLocation = getDefaultLocation();
        if (defaultLocation != null) {
            defaultLocation.setDefault(false);
            updateLocation(defaultLocation);
        }
        location.setDefault(true);
        return updateLocation(location);
    }

    public Location getDefaultLocation() {
        Location location = null;

        Cursor res = db.rawQuery("select * from " + DB_LOCATION_TABLE + " where " + LOC_KEY_DEFAULT + "=1", null);
        if (res.getCount() > 0) {
            res.moveToFirst();
            location = new Location();
            location.setLocationId(res.getInt(res.getColumnIndex(LOC_KEY_ID)));
            location.setCustomName(res.getString(res.getColumnIndex(LOC_KEY_NAME)));
            location.setLocationName(res.getString(res.getColumnIndex(LOC_KEY_LOCATION_NAME)));
            location.setCountryCode(res.getString(res.getColumnIndex(LOC_KEY_COUNTRY)));
            if (res.getInt(res.getColumnIndex(LOC_KEY_DEFAULT)) != 0) {
                location.setDefault(true);
            }
        }
        res.close();

        return location;
    }

    public long insertWeather(WeatherData location, boolean forecastData) {
        ContentValues values = new ContentValues();
        values.put(WEA_KEY_LOCATION, location.getLocationId());
        values.put(WEA_KEY_TEMP, location.getTemperature());
        values.put(WEA_KEY_PRESSURE, location.getPressure());
        values.put(WEA_KEY_HUMIDITY, location.getHumidity());
        values.put(WEA_KEY_DATE, location.getForecastDate().getTime() / 1000l);
        values.put(WEA_KEY_WEATHER_ID, location.getWeatherId());
        values.put(WEA_KEY_CLOUDS, location.getCloudLevel());
        values.put(WEA_KEY_WIND_DEG, location.getWindAngle());
        values.put(WEA_KEY_WIND_SPEED, location.getWindSpeed());
        values.put(WEA_KEY_RAIN, location.getRainLevel());
        values.put(WEA_KEY_SNOW, location.getSnowLevel());
        if (location.getSunrise() != null) {
            values.put(WEA_KEY_SUNRISE, location.getSunrise().getTime() / 1000l);
        }
        if (location.getSunset() != null) {
            values.put(WEA_KEY_SUNSET, location.getSunset().getTime() / 1000l);
        }
        values.put(WEA_KEY_ICON, location.getIcon().getName());
        return db.insert(DB_WEATHER_TABLE, null, values);
    }

    public boolean deleteWeather(WeatherData location) {
        return deleteWeather(location.getLocationId());
    }

    public boolean deleteWeather(long id) {
        String where = WEA_KEY_ID + "=" + id;
        return db.delete(DB_WEATHER_TABLE, where, null) > 0;
    }

    public boolean deleteAllWeatherForLocation(long id) {
        String where = WEA_KEY_LOCATION + "=" + id;
        return db.delete(DB_WEATHER_TABLE, where, null) > 0;
    }

    public List<WeatherData> getForecastForLocation(Location location) {
        List<WeatherData> list = new ArrayList<>();

        Cursor res = db.rawQuery("select * from " + DB_WEATHER_TABLE + " where " + WEA_KEY_LOCATION + "=" + location.getLocationId() + " " +
                "and (" + WEA_KEY_DATE + ">=" + (new Date().getTime() / 1000l) + " or " +
                WEA_KEY_ID + " in (select " + WEA_KEY_ID + " from " + DB_WEATHER_TABLE + " where " + WEA_KEY_LOCATION + "=" + location.getLocationId() + " " +
                "and " + WEA_KEY_DATE + "<" + (new Date().getTime() / 1000l) + " order by " + WEA_KEY_DATE + " desc limit 0,1 )) " +
                "order by " + WEA_KEY_DATE, null);
        res.moveToFirst();

        WeatherData tmp;
        while (res.isAfterLast() == false) {
            tmp = new WeatherData(location);
            tmp.setId(res.getInt(res.getColumnIndex(WEA_KEY_ID)));
            tmp.setTemperature(res.getDouble(res.getColumnIndex(WEA_KEY_TEMP)));
            tmp.setPressure(res.getDouble(res.getColumnIndex(WEA_KEY_PRESSURE)));
            tmp.setHumidity(res.getDouble(res.getColumnIndex(WEA_KEY_HUMIDITY)));
            tmp.setForecastDate(new Date(res.getInt(res.getColumnIndex(WEA_KEY_DATE)) * 1000l));
            tmp.setWeatherId(res.getInt(res.getColumnIndex(WEA_KEY_WEATHER_ID)));
            tmp.setIcon(getIcon(res.getString(res.getColumnIndex(WEA_KEY_ICON))));
            tmp.setCloudLevel(res.getDouble(res.getColumnIndex(WEA_KEY_CLOUDS)));
            tmp.setWindAngle(res.getDouble(res.getColumnIndex(WEA_KEY_WIND_DEG)));
            tmp.setWindSpeed(res.getDouble(res.getColumnIndex(WEA_KEY_WIND_SPEED)));
            tmp.setRainLevel(res.getDouble(res.getColumnIndex(WEA_KEY_RAIN)));
            tmp.setSnowLevel(res.getDouble(res.getColumnIndex(WEA_KEY_SNOW)));
            tmp.setSunrise(new Date(res.getInt(res.getColumnIndex(WEA_KEY_SUNRISE)) * 1000l));
            tmp.setSunset(new Date(res.getInt(res.getColumnIndex(WEA_KEY_SUNSET)) * 1000l));
            list.add(tmp);
            res.moveToNext();
        }
        res.close();
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requested forecast for location with ID = " + location.getLocationId() + ". Found " + list.size());
        if (list.isEmpty()) {
            list = connection.fetchForecastForLocationSync(location);
            if (list == null) {
                return null;
            }
            for (WeatherData data : list) {
                data.setIcon(getIcon(data.getWeatherIconName()));
                insertWeather(data, true);
            }
        }
        return list;
    }

    public WeatherData getWeatherDataForLocation(Location location) {
        Cursor res = db.rawQuery("select * from " + DB_WEATHER_TABLE + " where " + WEA_KEY_LOCATION + "=" + location.getLocationId() + " " +
                "and " + WEA_KEY_DATE + "<" + (new Date().getTime() / 1000l) + " order by " + WEA_KEY_DATE + " desc", null);
        res.moveToFirst();

        WeatherData tmp = null;
        if (res.getCount() > 0) {
            tmp = new WeatherData(location);
            tmp.setId(res.getInt(res.getColumnIndex(WEA_KEY_ID)));
            tmp.setTemperature(res.getDouble(res.getColumnIndex(WEA_KEY_TEMP)));
            tmp.setPressure(res.getDouble(res.getColumnIndex(WEA_KEY_PRESSURE)));
            tmp.setHumidity(res.getDouble(res.getColumnIndex(WEA_KEY_HUMIDITY)));
            tmp.setForecastDate(new Date(res.getInt(res.getColumnIndex(WEA_KEY_DATE)) * 1000l));
            tmp.setWeatherId(res.getInt(res.getColumnIndex(WEA_KEY_WEATHER_ID)));
            tmp.setIcon(getIcon(res.getString(res.getColumnIndex(WEA_KEY_ICON))));
            tmp.setCloudLevel(res.getDouble(res.getColumnIndex(WEA_KEY_CLOUDS)));
            tmp.setWindAngle(res.getDouble(res.getColumnIndex(WEA_KEY_WIND_DEG)));
            tmp.setWindSpeed(res.getDouble(res.getColumnIndex(WEA_KEY_WIND_SPEED)));
            tmp.setRainLevel(res.getDouble(res.getColumnIndex(WEA_KEY_RAIN)));
            tmp.setSnowLevel(res.getDouble(res.getColumnIndex(WEA_KEY_SNOW)));
            tmp.setSunrise(new Date(res.getInt(res.getColumnIndex(WEA_KEY_SUNRISE)) * 1000l));
            tmp.setSunset(new Date(res.getInt(res.getColumnIndex(WEA_KEY_SUNSET)) * 1000l));
        }
        res.close();
        if (tmp == null) {
            tmp = connection.fetchWeatherDataForLocationSync(location);
            if (tmp == null) {
                return null;
            }
            if (getLocation(location.getLocationId()) == null) {
                insertLocation(location);
            } else {
                deleteAllWeatherForLocation(location.getLocationId());
            }
            tmp.setIcon(getIcon(tmp.getWeatherIconName()));
            insertWeather(tmp, false);
            List<WeatherData> list = connection.fetchForecastForLocationSync(location);
            if (list != null) {
                for (WeatherData data : list) {
                    data.setIcon(getIcon(data.getWeatherIconName()));
                    insertWeather(data, true);
                }
            }
        }
        return tmp;
    }

    public WeatherData getWeatherData(int id) {
        Cursor res = db.rawQuery("select * from " + DB_WEATHER_TABLE + " where " + WEA_KEY_ID + "=" + id + " ", null);
        res.moveToFirst();

        WeatherData tmp = null;
        if (res.getCount() > 0) {
            tmp = new WeatherData(new Location(res.getInt(res.getColumnIndex(WEA_KEY_LOCATION))));
            tmp.setId(res.getInt(res.getColumnIndex(WEA_KEY_ID)));
            tmp.setTemperature(res.getDouble(res.getColumnIndex(WEA_KEY_TEMP)));
            tmp.setPressure(res.getDouble(res.getColumnIndex(WEA_KEY_PRESSURE)));
            tmp.setHumidity(res.getDouble(res.getColumnIndex(WEA_KEY_HUMIDITY)));
            tmp.setForecastDate(new Date(res.getInt(res.getColumnIndex(WEA_KEY_DATE)) * 1000l));
            tmp.setWeatherId(res.getInt(res.getColumnIndex(WEA_KEY_WEATHER_ID)));
            tmp.setIcon(getIcon(res.getString(res.getColumnIndex(WEA_KEY_ICON))));
            tmp.setCloudLevel(res.getDouble(res.getColumnIndex(WEA_KEY_CLOUDS)));
            tmp.setWindAngle(res.getDouble(res.getColumnIndex(WEA_KEY_WIND_DEG)));
            tmp.setWindSpeed(res.getDouble(res.getColumnIndex(WEA_KEY_WIND_SPEED)));
            tmp.setRainLevel(res.getDouble(res.getColumnIndex(WEA_KEY_RAIN)));
            tmp.setSnowLevel(res.getDouble(res.getColumnIndex(WEA_KEY_SNOW)));
            tmp.setSunrise(new Date(res.getInt(res.getColumnIndex(WEA_KEY_SUNRISE)) * 1000l));
            tmp.setSunset(new Date(res.getInt(res.getColumnIndex(WEA_KEY_SUNSET)) * 1000l));
        }
        res.close();

        if (tmp != null) {
            tmp.setLocation(getLocation(tmp.getLocationId()));
        }

        return tmp;
    }

    private static class IntWrapper {
        private int a;

        public IntWrapper(int a) {
            this.a = a;
        }

        public void add(int b) {
            a += b;
        }

        public int getNumber() {
            return a;
        }
    }

    public boolean updateAllDataAsync() {
        final List<Location> locations = getAllLocations();
        final IntWrapper count = new IntWrapper(locations.size());
        final IntWrapper failed = new IntWrapper(0);
        for (final Location l : locations) {
            connection.fetchWeatherDataForLocation(l, new EventListener<WeatherData>() {
                @Override
                public void onEvent(WeatherData weatherData) {
                    final WeatherData data = weatherData;
                    if (data == null) {
                        synchronized (count) {
                            count.add(-1);
                        }
                        failed.add(1);
                        synchronized (count) {
                            if (count.getNumber() <= 0) {
                                EventBus.getDefault().post(new WeatherUpdateEvent(false));
                                dbHelper.close();
                                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Async data update ended. Reason: failed to get weather data for location " + l.getLocationId());
                            }
                        }
                        return;
                    }
                    getIconAsync(data.getWeatherIconName(), new EventListener<WeatherIcon>() {
                        @Override
                        public void onEvent(WeatherIcon bitmap) {
                            data.setIcon(bitmap);
                            if (data.getIcon() == null) {
                                synchronized (count) {
                                    count.add(-1);
                                }
                                failed.add(1);
                                synchronized (count) {
                                    if (count.getNumber() <= 0) {
                                        EventBus.getDefault().post(new WeatherUpdateEvent(false));
                                        dbHelper.close();
                                        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Async data update ended. Reason: failed to get following weather icon - " + data.getWeatherIconName());
                                    }
                                }
                                return;
                            }
                            connection.fetchForecastForLocation(l, new EventListener<List<WeatherData>>() {
                                @Override
                                public void onEvent(List<WeatherData> weatherDatas) {
                                    final List<WeatherData> dataList = weatherDatas;
                                    if (dataList == null) {
                                        synchronized (count) {
                                            count.add(-1);
                                        }
                                        failed.add(1);
                                        synchronized (count) {
                                            if (count.getNumber() <= 0) {
                                                EventBus.getDefault().post(new WeatherUpdateEvent(false));
                                                dbHelper.close();
                                                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Async data update ended. Reason: failed to get forecast for location " + l.getLocationId());
                                            }
                                        }
                                        return;
                                    }
                                    final IntWrapper innerCount = new IntWrapper(dataList.size());
                                    for (final WeatherData wd : dataList) {
                                        getIconAsync(wd.getWeatherIconName(), new EventListener<WeatherIcon>() {
                                            @Override
                                            public void onEvent(WeatherIcon bitmap) {
                                                wd.setIcon(bitmap);
                                                if (wd.getIcon() == null) {
                                                    synchronized (count) {
                                                        count.add(-1);
                                                    }
                                                    failed.add(1);
                                                    synchronized (count) {
                                                        if (count.getNumber() <= 0) {
                                                            EventBus.getDefault().post(new WeatherUpdateEvent(false));
                                                            dbHelper.close();
                                                            Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Async data update ended. Reason: failed to get following forecast icon - " + data.getWeatherIconName());
                                                        }
                                                    }
                                                    return;
                                                }
                                                innerCount.add(-1);
                                                if (innerCount.getNumber() <= 0) {
                                                    deleteAllWeatherForLocation(l.getLocationId());
                                                    insertWeather(data, false);
                                                    for (WeatherData wd : dataList) {
                                                        insertWeather(wd, true);
                                                    }
                                                    synchronized (count) {
                                                        count.add(-1);
                                                        if (count.getNumber() <= 0) {
                                                            EventBus.getDefault().post(new WeatherUpdateEvent(failed.getNumber() == 0));
                                                            dbHelper.close();
                                                            Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Async data update ended. Reason: " + ((failed.getNumber() == 0)? "success!" : "failed due to earlier error."));
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
        return true;
    }

    public long insertWeatherIcon(String name, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(ICON_KEY_NAME, name);
        values.put(ICON_KEY_BITMAP, BitmapUtility.getBytes(bitmap));
        return db.insert(DB_ICON_TABLE, null, values);
    }

    public WeatherIcon getIcon(String name) {
        WeatherIcon icon = null;
        Cursor res = db.rawQuery("select * from " + DB_ICON_TABLE + " where " + ICON_KEY_NAME + "=\"" + name + "\"", null);
        if (res.getCount() > 0) {
            res.moveToFirst();
            icon = new WeatherIcon();
            icon.setName(res.getString(res.getColumnIndex(ICON_KEY_NAME)));
            icon.setBitmap(BitmapUtility.getImage(res.getBlob(res.getColumnIndex(ICON_KEY_BITMAP))));
        }
        res.close();

        if (icon != null) {
            return icon;
        } else {
            Bitmap bitmap = connection.fetchImageSync(name);
            if (bitmap != null) {
                insertWeatherIcon(name, bitmap);
                return new WeatherIcon(name, bitmap);
            }
            return null;
        }
    }

    public void getIconAsync(final String name, final EventListener<WeatherIcon> listener) {
        WeatherIcon icon = null;
        Cursor res = db.rawQuery("select * from " + DB_ICON_TABLE + " where " + ICON_KEY_NAME + "=\"" + name + "\"", null);
        if (res.getCount() > 0) {
            res.moveToFirst();
            icon = new WeatherIcon();
            icon.setBitmap(BitmapUtility.getImage(res.getBlob(res.getColumnIndex(ICON_KEY_BITMAP))));
            icon.setName(name);
        }
        res.close();

        if (icon != null) {
            if (listener != null) {
                listener.onEvent(icon);
            }
            return;
        } else {
            connection.fetchImage(name, new EventListener<Bitmap>() {
                @Override
                public void onEvent(Bitmap bitmap) {
                    if (bitmap != null) {
                        insertWeatherIcon(name, bitmap);
                        listener.onEvent(new WeatherIcon(name, bitmap));
                    }
                    listener.onEvent(null);
                }
            });
        }
    }

    public long insertAlarm(AlarmData data) {
        ContentValues values = new ContentValues();
        values.put(ALARM_KEY_NAME, data.getName());
        values.put(ALARM_KEY_TIME, data.getDate().getTime() / 1000l);
        values.put(ALARM_KEY_RINGTONE, data.getRingtone());
        values.put(ALARM_KEY_VOLUME, data.getRingtoneVolume());
        values.put(ALARM_KEY_REPEAT, data.getRepeatMode());
        values.put(ALARM_KEY_ENABLED, data.isEnabled());
        values.put(ALARM_KEY_SHUT_OFF, data.getShutOffMethod().getValue());
        return db.insert(DB_ALARM_TABLE, null, values);
    }

    public boolean deleteAlarm(int id) {
        String where = ALARM_KEY_ID + "=" + id;
        return db.delete(DB_ALARM_TABLE, where, null) > 0;
    }

    public boolean updateAlarm(AlarmData data) {
        String where = ALARM_KEY_ID + "=" + data.getAlarmId();
        ContentValues values = new ContentValues();
        values.put(ALARM_KEY_NAME, data.getName());
        values.put(ALARM_KEY_TIME, data.getDate().getTime() / 1000l);
        values.put(ALARM_KEY_RINGTONE, data.getRingtone());
        values.put(ALARM_KEY_VOLUME, data.getRingtoneVolume());
        values.put(ALARM_KEY_REPEAT, data.getRepeatMode());
        values.put(ALARM_KEY_ENABLED, data.isEnabled() ? 1 : 0);
        values.put(ALARM_KEY_SHUT_OFF, data.getShutOffMethod().getValue());
        return db.update(DB_ALARM_TABLE, values, where, null) > 0;
    }

    public AlarmData getAlarm(int id) {
        AlarmData data = null;
        Cursor res = db.rawQuery("select * from " + DB_ALARM_TABLE + " where " + ALARM_KEY_ID + "=" + id + "", null);
        if (res.getCount() > 0) {
            res.moveToFirst();
            data = new AlarmData();
            data.setAlarmId(id);
            data.setName(res.getString(res.getColumnIndex(ALARM_KEY_NAME)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(ALARM_KEY_TIME)) * 1000l));
            data.setRingtone(res.getString(res.getColumnIndex(ALARM_KEY_RINGTONE)));
            data.setRingtoneVolume(res.getInt(res.getColumnIndex(ALARM_KEY_VOLUME)));
            data.setRepeatMode(res.getInt(res.getColumnIndex(ALARM_KEY_REPEAT)));
            data.setEnabled(res.getInt(res.getColumnIndex(ALARM_KEY_ENABLED)) != 0);
            data.setShutOffMethod(AlarmShutOffMethod.valueOf(res.getInt(res.getColumnIndex(ALARM_KEY_SHUT_OFF))));
        }
        res.close();

        return data;
    }

    public List<AlarmData> getAlarms() {
        List<AlarmData> list = new LinkedList<>();
        AlarmData data = null;
        Cursor res = db.rawQuery("select * from " + DB_ALARM_TABLE + " order by " + ALARM_KEY_ENABLED + " desc, " + ALARM_KEY_TIME + " asc, " + ALARM_KEY_NAME + " asc", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new AlarmData();
            data.setAlarmId(res.getInt(res.getColumnIndex(ALARM_KEY_ID)));
            data.setName(res.getString(res.getColumnIndex(ALARM_KEY_NAME)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(ALARM_KEY_TIME)) * 1000l));
            data.setRingtone(res.getString(res.getColumnIndex(ALARM_KEY_RINGTONE)));
            data.setRingtoneVolume(res.getInt(res.getColumnIndex(ALARM_KEY_VOLUME)));
            data.setRepeatMode(res.getInt(res.getColumnIndex(ALARM_KEY_REPEAT)));
            data.setEnabled(res.getInt(res.getColumnIndex(ALARM_KEY_ENABLED)) != 0);
            data.setShutOffMethod(AlarmShutOffMethod.valueOf(res.getInt(res.getColumnIndex(ALARM_KEY_SHUT_OFF))));
            list.add(data);
            res.moveToNext();
        }
        res.close();

        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting all alarms. Found " + list.size());
        return list;
    }

    public List<AlarmData> getAllEnabledAlarms() {
        List<AlarmData> list = new LinkedList<>();
        AlarmData data = null;
        Cursor res = db.rawQuery("select * from " + DB_ALARM_TABLE + " where " + ALARM_KEY_ENABLED + "=1" +
                " order by " + ALARM_KEY_TIME + " asc, " + ALARM_KEY_NAME + " asc", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new AlarmData();
            data.setAlarmId(res.getInt(res.getColumnIndex(ALARM_KEY_ID)));
            data.setName(res.getString(res.getColumnIndex(ALARM_KEY_NAME)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(ALARM_KEY_TIME)) * 1000l));
            data.setRingtone(res.getString(res.getColumnIndex(ALARM_KEY_RINGTONE)));
            data.setRingtoneVolume(res.getInt(res.getColumnIndex(ALARM_KEY_VOLUME)));
            data.setRepeatMode(res.getInt(res.getColumnIndex(ALARM_KEY_REPEAT)));
            data.setEnabled(res.getInt(res.getColumnIndex(ALARM_KEY_ENABLED)) != 0);
            data.setShutOffMethod(AlarmShutOffMethod.valueOf(res.getInt(res.getColumnIndex(ALARM_KEY_SHUT_OFF))));
            list.add(data);
            res.moveToNext();
        }
        res.close();

        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting all enabled alarms. Found " + list.size());
        return list;
    }

    public long insertEvent(EventData data) {
        ContentValues values = new ContentValues();
        values.put(EVE_KEY_NAME, data.getName());
        values.put(EVE_KEY_DESC, data.getDesc());
        values.put(EVE_KEY_DATE, data.getDate().getTime() / 1000l);
        values.put(EVE_KEY_NOTIFY, data.getMinutesBeforeNotification());
        return db.insert(DB_EVE_TABLE, null, values);
    }

    public boolean deleteEvent(int id) {
        String where = EVE_KEY_ID + "=" + id;
        return db.delete(DB_EVE_TABLE, where, null) > 0;
    }

    public boolean updateEvent(EventData data) {
        String where = EVE_KEY_ID + "=" + data.getId();
        ContentValues values = new ContentValues();
        values.put(EVE_KEY_NAME, data.getName());
        values.put(EVE_KEY_DESC, data.getDesc());
        values.put(EVE_KEY_DATE, data.getDate().getTime() / 1000l);
        values.put(EVE_KEY_NOTIFY, data.getMinutesBeforeNotification());
        return db.update(DB_EVE_TABLE, values, where, null) > 0;
    }

    public EventData getEvent(int id) {
        EventData data = null;
        Cursor res = db.rawQuery("select * from " + DB_EVE_TABLE + " where " + EVE_KEY_ID + "=" + id + "", null);
        if (res.getCount() > 0) {
            res.moveToFirst();
            data = new EventData();
            data.setId(id);
            data.setName(res.getString(res.getColumnIndex(EVE_KEY_NAME)));
            data.setDesc(res.getString(res.getColumnIndex(EVE_KEY_DESC)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(EVE_KEY_DATE)) * 1000l));
            data.setMinutesBeforeNotification(res.getInt(res.getColumnIndex(EVE_KEY_NOTIFY)));
        }
        res.close();

        return data;
    }

    public List<EventData> getEvents() {
        List<EventData> list = new LinkedList<>();
        EventData data = null;
        Cursor res = db.rawQuery("select * from " + DB_EVE_TABLE + " order by " + EVE_KEY_DATE + ", " + EVE_KEY_NAME + "", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new EventData();
            data.setId(res.getInt(res.getColumnIndex(EVE_KEY_ID)));
            data.setName(res.getString(res.getColumnIndex(EVE_KEY_NAME)));
            data.setDesc(res.getString(res.getColumnIndex(EVE_KEY_DESC)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(EVE_KEY_DATE)) * 1000l));
            data.setMinutesBeforeNotification(res.getInt(res.getColumnIndex(EVE_KEY_NOTIFY)));
            list.add(data);
            res.moveToNext();
        }
        res.close();

        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting all events. Found " + list.size());
        return list;
    }

    public List<EventData> getEventsPast() {
        List<EventData> list = new LinkedList<>();
        EventData data = null;
        Cursor res = db.rawQuery("select * from " + DB_EVE_TABLE + " where " + EVE_KEY_DATE + "<" + ((new Date()).getTime() / 1000l) +
                " order by " + EVE_KEY_DATE + " desc, " + EVE_KEY_NAME + "", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new EventData();
            data.setId(res.getInt(res.getColumnIndex(EVE_KEY_ID)));
            data.setName(res.getString(res.getColumnIndex(EVE_KEY_NAME)));
            data.setDesc(res.getString(res.getColumnIndex(EVE_KEY_DESC)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(EVE_KEY_DATE)) * 1000l));
            data.setMinutesBeforeNotification(res.getInt(res.getColumnIndex(EVE_KEY_NOTIFY)));
            list.add(data);
            res.moveToNext();
        }
        res.close();

        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting all past events. Found " + list.size());
        return list;
    }

    public List<EventData> getEventsFuture() {
        List<EventData> list = new LinkedList<>();
        EventData data = null;
        Cursor res = db.rawQuery("select * from " + DB_EVE_TABLE + " where " + EVE_KEY_DATE + ">=" + ((new Date()).getTime() / 1000l) +
                " order by " + EVE_KEY_DATE + ", " + EVE_KEY_NAME + "", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new EventData();
            data.setId(res.getInt(res.getColumnIndex(EVE_KEY_ID)));
            data.setName(res.getString(res.getColumnIndex(EVE_KEY_NAME)));
            data.setDesc(res.getString(res.getColumnIndex(EVE_KEY_DESC)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(EVE_KEY_DATE)) * 1000l));
            data.setMinutesBeforeNotification(res.getInt(res.getColumnIndex(EVE_KEY_NOTIFY)));
            list.add(data);
            res.moveToNext();
        }
        res.close();

        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting all future events. Found " + list.size());
        return list;
    }

    public List<EventData> getEventsInDay(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int first = (int) (calendar.getTime().getTime() / 1000l);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        int second = (int) (calendar.getTime().getTime() / 1000l);
        List<EventData> list = new LinkedList<>();
        EventData data = null;
        Cursor res = db.rawQuery("select * from " + DB_EVE_TABLE + " where " + EVE_KEY_DATE + ">=" + first + " and " + EVE_KEY_DATE + "<" + second +
                " order by " + EVE_KEY_DATE + ", " + EVE_KEY_NAME + "", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            data = new EventData();
            data.setId(res.getInt(res.getColumnIndex(EVE_KEY_ID)));
            data.setName(res.getString(res.getColumnIndex(EVE_KEY_NAME)));
            data.setDesc(res.getString(res.getColumnIndex(EVE_KEY_DESC)));
            data.setDate(new Date(res.getInt(res.getColumnIndex(EVE_KEY_DATE)) * 1000l));
            data.setMinutesBeforeNotification(res.getInt(res.getColumnIndex(EVE_KEY_NOTIFY)));
            list.add(data);
            res.moveToNext();
        }
        res.close();

        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Requesting all events on " + day + "-" + (month + 1) + "-" + year + ". Found " + list.size());
        return list;
    }
}
