package pl.mbud.everydayhelper.data;

/**
 * Created by Maciek on 30.12.2016.
 */

public class Location {
    private static Location defaultLocation = null;

    private String customName;
    private String locationName;
    private String countryCode;
    private Integer locationId;
    private Coordinate coordinate;
    private boolean isDefault = false;

    public Location() {

    }

    public Location(int locationId) {
        this.locationId = locationId;
    }

    public Location(String locationName) {
        this.customName = locationName;
    }

    public Location(String locationName, String countryCode) {
        this.locationName = locationName;
        this.countryCode = countryCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isDefaultLocation() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public static Location getDefaultLocation() {
        return defaultLocation;
    }

    public static void setDefaultLocation(Location defaultLocation) {
        if (Location.defaultLocation != null) {
            Location.defaultLocation.isDefault = false;
        }
        Location.defaultLocation = defaultLocation;
        defaultLocation.isDefault = true;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
