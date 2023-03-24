package zfani.assaf.radio_kahol_lavan_tv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.model.Broadcast;

public class BroadcastAdapter extends ArrayAdapter<Broadcast> {

    public BroadcastAdapter(@NonNull Context context, @NonNull List<Broadcast> broadcastList) {
        super(context, R.layout.list_item_broadcast, broadcastList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BroadcastViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_broadcast, parent, false);
            viewHolder = new BroadcastViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BroadcastViewHolder) convertView.getTag();
        }
        viewHolder.bindData(getItem(position), true);
        return convertView;
    }
}
