package pl.mbud.everydayhelper.events;

/**
 * Created by Maciek on 31.12.2016.
 */

public class WeatherUpdateEvent {
    private boolean success = true;

    public WeatherUpdateEvent() {
        this(true);
    }

    public WeatherUpdateEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
