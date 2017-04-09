package pl.mbud.everydayhelper.fragments.alarm;

import android.support.v4.app.Fragment;
import android.util.Log;

import pl.mbud.everydayhelper.BaseApplication;

/**
 * Created by Maciek on 03.01.2017.
 */

public abstract class AlarmShutOffFragment extends Fragment {

    public static final String DEBUG_TAG = "ALARM-SHUT-OFF";

    protected final void turnOffTheAlarm() {
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Turning off the alarm...");

        getActivity().finish();
    }
}
