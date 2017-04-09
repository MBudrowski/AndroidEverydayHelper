package pl.mbud.everydayhelper.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;

/**
 * Created by Maciek on 30.12.2016.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    private List<Location> locationList;
    private Context context;

    private List<RecyclerViewItemClickListener<Location>> listeners = new LinkedList<>();

    public LocationListAdapter(List<Location> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_list_item, parent, false);
        return new LocationListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Location location = locationList.get(position);

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RecyclerViewItemClickListener<Location> listener : listeners) {
                    listener.onItemClicked(view, location, position);
                }
            }
        });
        holder.getView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (RecyclerViewItemClickListener<Location> listener : listeners) {
                    listener.onItemLongClicked(view, location, position);
                }
                return true;
            }
        });
        holder.getName().setText(location.getCustomName());
        StringBuilder sb = new StringBuilder();
        sb.append(location.getLocationName() + ", " + location.getCountryCode());
        if (location.isDefaultLocation()) {
            sb.append(", " + context.getString(R.string.default_location));
        }
        holder.getDesc().setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void addOnItemClickListener(RecyclerViewItemClickListener<Location> listener) {
        listeners.add(listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, desc;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            name = (TextView) itemView.findViewById(R.id.location_list_item_name);
            desc = (TextView) itemView.findViewById(R.id.location_list_item_desc);
        }

        public View getView() {
            return view;
        }

        public TextView getDesc() {
            return desc;
        }

        public TextView getName() {
            return name;
        }
    }
}
