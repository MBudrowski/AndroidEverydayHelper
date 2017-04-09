package pl.mbud.everydayhelper.weather;

import android.content.Context;

import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 31.12.2016.
 */

public enum TemperatureScale {
    KELVIN, CELCIUS, FAHRENHEIT;

    public String getSuffix(Context context) {
        switch (this) {
            case CELCIUS:
                return context.getString(R.string.celcius);
            case FAHRENHEIT:
                return context.getString(R.string.fahrenheit);
            case KELVIN:
                return context.getString(R.string.kelvin);
        }
        return null;
    }
}
