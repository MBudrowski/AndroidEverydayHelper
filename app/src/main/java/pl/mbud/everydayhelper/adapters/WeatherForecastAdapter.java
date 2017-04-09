package pl.mbud.everydayhelper.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import pl.mbud.everydayhelper.BaseApplication;
import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;
import pl.mbud.everydayhelper.util.NumberFormatter;
import pl.mbud.everydayhelper.weather.TemperatureScale;
import pl.mbud.everydayhelper.weather.WeatherData;

/**
 * Created by Maciek on 30.12.2016.
 */

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {

    private List<WeatherData> weatherDataList;
    private Context context;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private List<RecyclerViewItemClickListener<WeatherData>> listeners = new LinkedList<>();

    public WeatherForecastAdapter(List<WeatherData> locationList, Context context) {
        this.weatherDataList = locationList;
        this.context = context;
    }

    @Override
    public WeatherForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_item, parent, false);
        return new WeatherForecastAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WeatherData data = weatherDataList.get(position);

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RecyclerViewItemClickListener<WeatherData> listener : listeners) {
                    listener.onItemClicked(view, data, position);
                }
            }
        });
        holder.getView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (RecyclerViewItemClickListener<WeatherData> listener : listeners) {
                    listener.onItemLongClicked(view, data, position);
                }
                return true;
            }
        });
        holder.getDate().setText(dateFormat.format(data.getForecastDate()));
        holder.getTemp().setText(data.getTemperature().toString());
        TemperatureScale scale = TemperatureScale.valueOf(BaseApplication.getInstance().getPreferences().getString("temperatureScale", "CELCIUS"));
        holder.getTemp().setText(NumberFormatter.getTemperatureFormatted(data.getTemperature(scale)) + scale.getSuffix(context));
        holder.getImageView().setImageBitmap(data.getIcon().getBitmap());
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    public void addOnItemClickListener(RecyclerViewItemClickListener<WeatherData> listener) {
        listeners.add(listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date, temp;
        private ImageView imageView;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            date = (TextView) itemView.findViewById(R.id.forecast_item_date);
            temp = (TextView) itemView.findViewById(R.id.forecast_item_temp);
            imageView = (ImageView) itemView.findViewById(R.id.forecast_item_image);
        }

        public View getView() {
            return view;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getDate() {
            return date;
        }

        public TextView getTemp() {
            return temp;
        }
    }
}
