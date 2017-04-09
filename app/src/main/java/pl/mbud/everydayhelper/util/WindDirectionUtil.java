package pl.mbud.everydayhelper.util;

import android.content.Context;

import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 05.01.2017.
 */

public class WindDirectionUtil {
    public static String getDirectionString(double degrees, Context context) {
        if (degrees >= 337.5 || degrees <= 22.5) {
            return context.getString(R.string.N);
        }
        if (degrees > 22.5 || degrees < 67.5) {
            return context.getString(R.string.NE);
        }
        if (degrees >= 67.5 || degrees <= 112.5) {
            return context.getString(R.string.E);
        }
        if (degrees > 112.5 || degrees < 157.5) {
            return context.getString(R.string.SE);
        }
        if (degrees >= 157.5 || degrees <= 202.5) {
            return context.getString(R.string.S);
        }
        if (degrees > 202.5 || degrees < 247.5) {
            return context.getString(R.string.SW);
        }
        if (degrees >= 247.5 || degrees < 292.5) {
            return context.getString(R.string.W);
        }
        if (degrees > 292.5 || degrees < 337.5) {
            return context.getString(R.string.NW);
        }
        return "";
    }
}
