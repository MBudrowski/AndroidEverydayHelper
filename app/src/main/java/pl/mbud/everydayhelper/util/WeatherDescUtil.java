package pl.mbud.everydayhelper.util;

import android.content.Context;
import android.util.Log;

import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 05.01.2017.
 */

public class WeatherDescUtil {
    public static String getWeatherDescriptionFromId(int id, Context context) {
        String[] strings = context.getResources().getStringArray(R.array.weatherCodes);
        String[] tmp;
        int code;
        for (String s : strings) {
            tmp = s.split("\\|");
            try {
                code = Integer.parseInt(tmp[0]);
                if (code == id) {
                    return tmp[1];
                }
            }
            catch (Exception e) {
                continue;
            }
        }
        return "";
    }
}
