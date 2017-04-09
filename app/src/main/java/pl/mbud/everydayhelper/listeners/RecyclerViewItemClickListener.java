package pl.mbud.everydayhelper.listeners;

import android.view.View;

/**
 * Created by Maciek on 30.12.2016.
 */

public interface RecyclerViewItemClickListener<T> {
    public void onItemClicked(View view, T item, int position);

    public void onItemLongClicked(View view, T item, int position);
}
