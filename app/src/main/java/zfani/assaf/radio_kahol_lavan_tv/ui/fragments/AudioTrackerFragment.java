package zfani.assaf.radio_kahol_lavan_tv.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.adapters.SongAdapter;
import zfani.assaf.radio_kahol_lavan_tv.base.BaseFragment;
import zfani.assaf.radio_kahol_lavan_tv.viewmodel.AudioTrackViewModel;

public class AudioTrackerFragment extends BaseFragment {

    private final Handler handler = new Handler();
    private ListView lvSongList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_audio_track, container, false);
        lvSongList = root.findViewById(R.id.lvSongList);
        lvSongList.setOnKeyListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(new Runnable() {
            @Override
            public void run() {
                new ViewModelProvider(requireActivity()).get(AudioTrackViewModel.class).getAudioTrack().observe(getViewLifecycleOwner(), songs -> lvSongList.setAdapter(new SongAdapter(requireContext(), songs)));
                handler.postDelayed(this, 60000);
            }
        });
    }
}
