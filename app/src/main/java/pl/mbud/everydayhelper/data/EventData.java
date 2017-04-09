package pl.mbud.everydayhelper.data;

import android.util.EventLog;

import java.util.Date;

/**
 * Created by Maciek on 04.01.2017.
 */

public class EventData {
    private Integer id;
    private String name, desc;
    private Date date;
    private Integer minutesBeforeNotification;

    public EventData() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getMinutesBeforeNotification() {
        return minutesBeforeNotification;
    }

    public void setMinutesBeforeNotification(Integer minutesBeforeNotification) {
        this.minutesBeforeNotification = minutesBeforeNotification;
    }
}
