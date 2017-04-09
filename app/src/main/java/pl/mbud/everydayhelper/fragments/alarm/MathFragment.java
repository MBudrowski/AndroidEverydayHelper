package pl.mbud.everydayhelper.fragments.alarm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 03.01.2017.
 */

public class MathFragment extends AlarmShutOffFragment {

    private Button button;
    private EditText result;
    private TextView equation;

    private Random random;
    private int a, b, op;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_math,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        random = new Random();

        equation = (TextView) getView().findViewById(R.id.alarm_turn_off_equation);
        result = (EditText) getView().findViewById(R.id.alarm_turn_off_result);

        randomizeEquation();

        button = (Button) getView().findViewById(R.id.alarm_turn_off_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.getText().toString().isEmpty()) {
                    return;
                }
                try {
                    if (Integer.parseInt(result.getText().toString()) == getResult()) {
                        Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Correct result!");
                        turnOffTheAlarm();
                        return;
                    }
                } catch (NumberFormatException e) {
                }
                Log.d(BaseApplication.DEBUG_TAG + "/" + DEBUG_TAG, "Wrong result!");

                randomizeEquation();
            }
        });
    }

    private void randomizeEquation() {
        a = random.nextInt(9) + 2;
        b = random.nextInt(9) + 2;
        op = random.nextInt(3);
        if (op == 1 && a < b) {
            int tmp = b;
            b = a;
            a = tmp;
        }
        char charOp = ' ';
        switch (op) {
            case 0:
                charOp = '+';
                break;
            case 1:
                charOp = '-';
                break;
            case 2:
                charOp = '*';
                break;
        }

        equation.setText(a + " " + charOp + " " + b + " = ");
        result.setText("");
    }

    private int getResult() {
        switch (op) {
            case 0:
                return a + b;
            case 1:
                return a - b;
            case 2:
                return a * b;
        }
        return -1;
    }
}
