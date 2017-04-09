package pl.mbud.everydayhelper.data;

import android.content.Context;

import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 03.01.2017.
 */

public enum AlarmShutOffMethod {
    BUTTON(0), MATH(1), SHAKE(2);

    private int i;

    AlarmShutOffMethod(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }

    public String getName(Context context) {
        switch (this) {
            case BUTTON:
                return context.getString(R.string.shut_off_button);
            case MATH:
                return context.getString(R.string.shut_off_math);
            case SHAKE:
                return context.getString(R.string.shut_off_shake);
        }
        return "";
    }

    public static AlarmShutOffMethod valueOf(int i) {
        switch (i) {
            case 0:
                return BUTTON;
            case 1:
                return MATH;
            case 2:
                return SHAKE;
        }
        return null;
    }
}
