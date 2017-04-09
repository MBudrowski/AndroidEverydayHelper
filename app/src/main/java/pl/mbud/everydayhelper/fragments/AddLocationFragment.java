package pl.mbud.everydayhelper.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;

/**
 * Created by Maciek on 31.12.2016.
 */

public class AddLocationFragment extends Fragment {

    private EditText editText;
    private AppCompatCheckBox checkBox;
    private LinearLayout locationSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_location,
                container, false);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editText = (EditText) getView().findViewById(R.id.fragment_add_location_name_edittext);
        checkBox = (AppCompatCheckBox) getView().findViewById(R.id.fragment_add_location_use_location_button);
        locationSection = (LinearLayout) getView().findViewById(R.id.fragment_add_location_use_location_section);

        if (!BaseApplication.getInstance().isLocationEnabled()) {
            checkBox.setEnabled(false);
            locationSection.setVisibility(View.INVISIBLE);
        }
        else {
            checkBox.setEnabled(true);
            locationSection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public EditText getEditText() {
        return editText;
    }

    public AppCompatCheckBox getCheckBox() {
        return checkBox;
    }

    public LinearLayout getLocationSection() {
        return locationSection;
    }
}
