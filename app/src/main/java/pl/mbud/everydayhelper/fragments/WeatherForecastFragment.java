package pl.mbud.everydayhelper.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.WeatherActivity;
import pl.mbud.everydayhelper.adapters.WeatherForecastAdapter;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;
import pl.mbud.everydayhelper.weather.WeatherData;

public class WeatherForecastFragment extends Fragment {

    private WeatherForecastAdapter adapter;
    private RecyclerView recyclerView;
    private int tries = 0;
    private List<WeatherData> weatherDataList = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_forecast,
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

        tries = 0;
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.forecast_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WeatherForecastAdapter(weatherDataList, getContext());
        recyclerView.setAdapter(adapter);
        adapter.addOnItemClickListener(new RecyclerViewItemClickListener<WeatherData>() {
            @Override
            public void onItemClicked(View view, WeatherData item, int position) {

                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra(WeatherInfoFragment.EXTRA_WEATHER_ID, item.getId());
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_NAME, item.getLocation().getCustomName());
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_ID, (item.getLocationId() == null) ? 0 : item.getLocationId().intValue());
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(View view, WeatherData item, int position) {
            }
        });

        if (getArguments() != null) {
            int id = getArguments().getInt(WeatherInfoFragment.EXTRA_LOCATION_ID);
            if (id != 0) {
                DatabaseHelper helper = BaseApplication.getInstance().getDbHelper();
                helper.open();
                List<WeatherData> list = helper.getForecastForLocation(new Location(id));
                helper.close();
                weatherDataList.addAll(list);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}