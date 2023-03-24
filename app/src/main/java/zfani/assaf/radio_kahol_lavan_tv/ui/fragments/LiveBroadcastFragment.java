package zfani.assaf.radio_kahol_lavan_tv.ui.fragments;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import zfani.assaf.radio_kahol_lavan_tv.App;
import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.adapters.BroadcastViewHolder;
import zfani.assaf.radio_kahol_lavan_tv.base.BaseFragment;
import zfani.assaf.radio_kahol_lavan_tv.model.Broadcast;

public class LiveBroadcastFragment extends BaseFragment {

    private final boolean isMain;

    public LiveBroadcastFragment(boolean isMain) {
        this.isMain = isMain;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_live_broadcast, container, false);
        TextView tvSongName = root.findViewById(R.id.tvSongName);
        tvSongName.setText(isMain ? R.string.app_name : R.string.radio_yemen);
        ImageView ivPlayOrPause = root.findViewById(R.id.ivPlayOrPause);
        BroadcastViewHolder broadcastViewHolder = new BroadcastViewHolder(root.findViewById(R.id.vBroadcast));
        App.songTitle.observe(getViewLifecycleOwner(), song -> {
            String day = App.daysArray[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
            Date now = new Date(System.currentTimeMillis());
            App.broadcasts.observe(getViewLifecycleOwner(), map -> {
                List<Broadcast> broadcastList = map.get(day);
                if (broadcastList != null) {
                    for (Broadcast broadcast : broadcastList) {
                        if (broadcast.startDate.before(now) && broadcast.endDate.after(now)) {
                            broadcastViewHolder.bindData(broadcast, isMain);
                        }
                    }
                }
            });
            tvSongName.setText(song);
            tvSongName.setSelected(true);
        });
        ivPlayOrPause.setOnClickListener(v -> {
            App.isPlaying.setValue(App.isPlaying.getValue() == null || !App.isPlaying.getValue());
            boolean isPlaying = App.isPlaying.getValue() != null && App.isPlaying.getValue();
            App.mediaPlayer.seekTo(0);
            App.mediaPlayer.setPlayWhenReady(!isPlaying);
            App.mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(isPlaying ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_PLAYING, 0, 1.0f).build());
            if (isPlaying) {
                App.audioManager.requestAudioFocus(i -> App.mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            } else {
                App.audioManager.abandonAudioFocus(i -> App.mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN));
            }
        });
        ivPlayOrPause.setOnKeyListener(this);
        ivPlayOrPause.setNextFocusUpId(isMain ? R.id.mainTab1 : R.id.mainTab3);
        App.isPlaying.observe(getViewLifecycleOwner(), isPlaying -> ivPlayOrPause.setImageResource(isPlaying ? R.drawable.ic_play : R.drawable.ic_pause));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        App.setMediaPlayerStreamingUrl(getContext(), isMain);
    }
}
