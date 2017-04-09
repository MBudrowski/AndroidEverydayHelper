package pl.mbud.everydayhelper.fragments.alarm;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.seismic.ShakeDetector;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 03.01.2017.
 */

public class ShakeFragment extends AlarmShutOffFragment implements ShakeDetector.Listener {

    private SensorManager sensorManager;
    private ShakeDetector detector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_shake,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        detector = new ShakeDetector(this);
        detector.setSensitivity(ShakeDetector.SENSITIVITY_HARD);
    }

    @Override
    public void onResume() {
        super.onResume();

        detector.start(sensorManager);
    }

    @Override
    public void onPause() {
        super.onPause();

        detector.stop();
    }

    @Override
    public void hearShake() {
        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Shake detected!");
        turnOffTheAlarm();
    }
}
