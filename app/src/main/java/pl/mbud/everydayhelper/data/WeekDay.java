package pl.mbud.everydayhelper.data;

import android.content.Context;

import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 02.01.2017.
 */

public enum WeekDay {
    MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(7), SUNDAY(1);

    private int index;

    WeekDay(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.weekDays)[getIndex() - 1];
    }

    public static WeekDay valueOf(int i) {
        switch (i) {
            case 1:
                return SUNDAY;
            case 2:
                return MONDAY;
            case 3:
                return TUESDAY;
            case 4:
                return WEDNESDAY;
            case 5:
                return THURSDAY;
            case 6:
                return FRIDAY;
            case 7:
                return SATURDAY;
        }
        return null;
    }
}