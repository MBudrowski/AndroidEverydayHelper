package pl.mbud.everydayhelper.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Maciek on 31.12.2016.
 */
public class NumberFormatter {
    private static NumberFormat tempFormat = new DecimalFormat("0.0");

    public static String getTemperatureFormatted(double temp) {
        return tempFormat.format(temp);
    }
}