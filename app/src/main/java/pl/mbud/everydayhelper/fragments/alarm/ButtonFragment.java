package pl.mbud.everydayhelper.fragments.alarm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 03.01.2017.
 */

public class ButtonFragment extends AlarmShutOffFragment {

    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_button,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button = (Button) getView().findViewById(R.id.alarm_turn_off_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Clicked the button!");
                turnOffTheAlarm();
            }
        });
    }
}
