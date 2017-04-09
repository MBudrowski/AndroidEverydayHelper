package pl.mbud.everydayhelper.data;

import java.util.Date;

/**
 * Created by Maciek on 02.01.2017.
 */

public class AlarmData {
    private int alarmId = 0;
    private String name;
    private Date date;
    private String ringtone;
    private int ringtoneVolume = 100;
    private int repeatMode = 0;
    private boolean enabled = false;
    private AlarmShutOffMethod shutOffMethod;

    public AlarmData() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public int getRingtoneVolume() {
        return ringtoneVolume;
    }

    public void setRingtoneVolume(int ringtoneVolume) {
        this.ringtoneVolume = ringtoneVolume;
    }

    public boolean isRepeating() {
        return repeatMode != 0;
    }

    public boolean isRepeating(WeekDay day) {
        return ((repeatMode >> (day.getIndex() - 1)) & 0x01) != 0;
    }

    public void addRepeatingDay(WeekDay day) {
        repeatMode |= (0x01 >> (day.getIndex() - 1));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AlarmShutOffMethod getShutOffMethod() {
        return shutOffMethod;
    }

    public void setShutOffMethod(AlarmShutOffMethod shutOffMethod) {
        this.shutOffMethod = shutOffMethod;
    }
}
