package pl.mbud.everydayhelper.data;

/**
 * Created by Maciek on 12.12.2016.
 */

public class Coordinate {
    private double longitude, latitude;

    public Coordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
