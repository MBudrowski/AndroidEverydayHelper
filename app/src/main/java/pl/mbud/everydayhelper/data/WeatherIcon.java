package pl.mbud.everydayhelper.data;

import android.graphics.Bitmap;

/**
 * Created by Maciek on 31.12.2016.
 */

public class WeatherIcon {
    private Bitmap bitmap;
    private String name;

    public WeatherIcon() {

    }

    public WeatherIcon(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setName(String name) {
        this.name = name;
    }
}
