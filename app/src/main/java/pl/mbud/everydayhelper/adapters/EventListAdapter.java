package pl.mbud.everydayhelper.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import pl.mbud.everydayhelper.R;
import pl.mbud.everydayhelper.data.EventData;
import pl.mbud.everydayhelper.data.WeekDay;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;

/**
 * Created by Maciek on 30.12.2016.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private List<EventData> eventList;
    private Context context;
    private DateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private List<RecyclerViewItemClickListener<EventData>> listeners = new LinkedList<>();

    public EventListAdapter(List<EventData> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new EventListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final EventData location = eventList.get(position);

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RecyclerViewItemClickListener<EventData> listener : listeners) {
                    listener.onItemClicked(view, location, position);
                }
            }
        });
        holder.getView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (RecyclerViewItemClickListener<EventData> listener : listeners) {
                    listener.onItemLongClicked(view, location, position);
                }
                return true;
            }
        });
        holder.getName().setText(location.getName());
        Calendar c = Calendar.getInstance();
        c.setTime(location.getDate());
        holder.getDesc().setText(WeekDay.valueOf(c.get(Calendar.DAY_OF_WEEK)).getName(context) + ", " + timeFormat.format(c.getTime()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addOnItemClickListener(RecyclerViewItemClickListener<EventData> listener) {
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
