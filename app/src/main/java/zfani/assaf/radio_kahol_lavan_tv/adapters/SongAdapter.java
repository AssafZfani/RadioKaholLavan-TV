package zfani.assaf.radio_kahol_lavan_tv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.model.Song;

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(@NonNull Context context, List<Song> songList) {
        super(context, R.layout.list_item_song, songList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView == null ? LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_song, parent, false) : convertView;
        Song song = getItem(position);
        if (song != null) {
            ((TextView) view.findViewById(R.id.tvSongName)).setText(song.getTitle());
        }
        return view;
    }
}
