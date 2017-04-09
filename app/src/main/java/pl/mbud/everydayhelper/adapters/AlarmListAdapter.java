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
import pl.mbud.everydayhelper.data.AlarmData;
import pl.mbud.everydayhelper.data.WeekDay;
import pl.mbud.everydayhelper.listeners.RecyclerViewItemClickListener;

/**
 * Created by Maciek on 30.12.2016.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {

    private List<AlarmData> alarmList;
    private Context context;
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private List<RecyclerViewItemClickListener<AlarmData>> listeners = new LinkedList<>();

    public AlarmListAdapter(List<AlarmData> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
    }

    @Override
    public AlarmListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);
        return new AlarmListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AlarmData location = alarmList.get(position);

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RecyclerViewItemClickListener<AlarmData> listener : listeners) {
                    listener.onItemClicked(view, location, position);
                }
            }
        });
        holder.getView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (RecyclerViewItemClickListener<AlarmData> listener : listeners) {
                    listener.onItemLongClicked(view, location, position);
                }
                return true;
            }
        });
        holder.getName().setText(location.getName());
        if (location.isEnabled()) {
            Calendar c = Calendar.getInstance();
            c.setTime(location.getDate());
            holder.getDesc().setText(timeFormat.format(location.getDate()) + ", " + WeekDay.valueOf(c.get(Calendar.DAY_OF_WEEK)).getName(context) + ", " + dateFormat.format(location.getDate()));
            holder.getRight().setText(context.getString(R.string.on));
        } else {
            holder.getDesc().setText(timeFormat.format(location.getDate()));
            holder.getRight().setText(context.getString(R.string.off));
        }
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void addOnItemClickListener(RecyclerViewItemClickListener<AlarmData> listener) {
        listeners.add(listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, desc, right;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            name = (TextView) itemView.findViewById(R.id.alarm_list_item_name);
            desc = (TextView) itemView.findViewById(R.id.alarm_list_item_desc);
            right = (TextView) itemView.findViewById(R.id.alarm_list_item_right);
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

        public TextView getRight() {
            return right;
        }
    }
}
