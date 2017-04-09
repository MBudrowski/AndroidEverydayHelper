package pl.mbud.everydayhelper.listeners;

/**
 * Created by Maciek on 01.01.2017.
 */

public interface EventListener<T> {
    public void onEvent(T t);
}
