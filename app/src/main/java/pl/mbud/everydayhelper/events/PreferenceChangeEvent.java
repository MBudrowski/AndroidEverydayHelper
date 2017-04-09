package pl.mbud.everydayhelper.events;

/**
 * Created by Maciek on 01.01.2017.
 */

public class PreferenceChangeEvent {
    private String name;

    public PreferenceChangeEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
