package pl.mbud.everydayhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import pl.mbud.everydayhelper.fragments.LocationListFragment;

/**
 * Created by Maciek on 30.12.2016.
 */

public class LocationListActivity extends BaseActivity {

    private Button addLocationButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        addLocationButton = (Button) findViewById(R.id.activity_location_list_add_location);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationListActivity.this);
                alertDialog.setTitle("Dodawanie lokacji");
                alertDialog.setMessage("Podaj nazwę lokacji:");

                final EditText input = new EditText(LocationListActivity.this);
                /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);*/
                /*alertDialog.setView(input);
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String locationName = input.getText().toString();
                                if (locationName != null && !locationName.isEmpty()) {
                                    Intent intent = new Intent(LocationListActivity.this, WeatherActivity.class);
                                    intent.putExtra(WeatherInfoFragment.EXTRA_LOCATION_NAME, locationName);
                                    startActivity(intent);
                                }
                            }
                        });

                alertDialog.setNegativeButton("Anuluj",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();*/
                Intent intent = new Intent(LocationListActivity.this, AddLocationActivity.class);
                startActivity(intent);
            }
        });

        Fragment fragInfo = new LocationListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_location_list_fragment, fragInfo);
        transaction.commit();
    }

    @Override
    protected Integer getMenuItemId() {
        return R.id.menu_general_weather;
    }
}
