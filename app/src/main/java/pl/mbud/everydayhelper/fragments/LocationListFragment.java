package pl.mbud.everydayhelper.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.EventInfoActivity;
import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.WeatherActivity;
import pl.mbud.everydayhelper.adapters.LocationListAdapter;
import pl.mbud.everydayhelper.alarm.EventNotificationAlarmManager;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.database.DatabaseHelper;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;

public class LocationListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noLocations;
    private LocationListAdapter adapter;
    private List<Location> locations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list,
                container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        noLocations = (TextView) getView().findViewById(R.id.no_locations);
        recyclerView = (RecyclerView) getView().findViewById(R.id.location_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LocationListAdapter(locations, getContext());
        recyclerView.setAdapter(adapter);
        adapter.addOnItemClickListener(new RecyclerViewItemClickListener<Location>() {
            @Override
            public void onItemClicked(View view, Location item, int position) {

                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_NAME, item.getCustomName());
                intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_ID, (item.getLocationId() == null) ? 0 : item.getLocationId().intValue());
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(View view, final Location location, int position) {
                PopupMenu menu = new PopupMenu(getActivity(), view);
                menu.getMenuInflater().inflate(R.menu.menu_location_item, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final DatabaseHelper dbHelper = BaseApplication.getInstance().getDbHelper();
                        switch (item.getItemId()) {
                            case R.id.menu_location_item_edit:
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle(getString(R.string.adding_location));
                                alertDialog.setMessage(getString(R.string.enter_new_name_location));

                                final EditText input = new EditText(getActivity());
                /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);*/
                                alertDialog.setView(input);
                                alertDialog.setPositiveButton(getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                String locationName = input.getText().toString();
                                                if (locationName != null && !locationName.isEmpty()) {
                                                    location.setCustomName(locationName);
                                                    dbHelper.open();
                                                    dbHelper.updateLocation(location);
                                                    dbHelper.close();
                                                    fetchLocations();
                                                }
                                            }
                                        });

                                alertDialog.setNegativeButton(getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                alertDialog.show();
                                return true;
                            case R.id.menu_location_item_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getString(R.string.confirm_deletion));
                                builder.setMessage(getString(R.string.are_you_sure_delete_location))
                                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dbHelper.open();
                                                dbHelper.deleteLocation(location);
                                                dbHelper.close();
                                                fetchLocations();
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                builder.show();
                                return true;
                            case R.id.menu_location_item_set_default:
                                dbHelper.open();
                                dbHelper.setDefaultLocation(location);
                                dbHelper.close();
                                fetchLocations();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                menu.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchLocations();
    }

    public void fetchLocations() {
        locations.clear();
        DatabaseHelper dbHelper = BaseApplication.getInstance().getDbHelper();
        dbHelper.open();
        locations.addAll(dbHelper.getAllLocations());
        if (locations.isEmpty()) {
            noLocations.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            noLocations.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        dbHelper.close();
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