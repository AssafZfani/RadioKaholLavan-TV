package zfani.assaf.radio_kahol_lavan_tv;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.icy.IcyInfo;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zfani.assaf.radio_kahol_lavan_tv.model.Broadcast;

public class App extends Application {

    public static final String[] daysArray = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    private static final String stream = "https://radiokahollavan.radioca.st/stream";
    public static MutableLiveData<Boolean> isPlaying;
    public static MutableLiveData<String> songTitle;
    public static MutableLiveData<HashMap<String, List<Broadcast>>> broadcasts;
    public static MediaSessionCompat mediaSession;
    public static SimpleExoPlayer mediaPlayer;
    public static AudioManager audioManager;
    static boolean isMain;

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        initAppInfo();
        initBroadcasts();
    }

    public static void setMediaPlayerStreamingUrl(Context context, boolean isMain) {
        if (isMain && App.isMain) return;
        App.isMain = isMain;
        mediaPlayer.prepare(new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name) + " TV")))
                .createMediaSource(Uri.parse(isMain ? context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE)
                        .getString("StreamingUrl", stream) : "https://radiokahollavan.com/yemenstream")));
        mediaPlayer.seekTo(0);
        mediaPlayer.setPlayWhenReady(Boolean.FALSE.equals(App.isPlaying.getValue()));
    }

    private void initMediaPlayer() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        isPlaying = new MutableLiveData<>(false);
        songTitle = new MutableLiveData<>();
        mediaPlayer = new SimpleExoPlayer.Builder(this).build();
        setMediaPlayerStreamingUrl(this, true);
        //mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MUSIC).build(), true);
        mediaPlayer.setWakeMode(C.WAKE_MODE_NETWORK);
        mediaSession = new MediaSessionCompat(this, getPackageName());
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onMetadata(@NonNull EventTime eventTime, @NonNull Metadata metadata) {
                IcyInfo info = (IcyInfo) metadata.get(0);
                songTitle.setValue(info.title);
                mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.drawable.banner))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Music is the answer").build());
            }
        });
        mediaSession.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();
                isPlaying.setValue(false);
                mediaPlayer.seekTo(0);
                mediaPlayer.setPlayWhenReady(true);
                App.audioManager.requestAudioFocus(i -> mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }

            @Override
            public void onPause() {
                super.onPause();
                isPlaying.setValue(true);
                mediaPlayer.seekTo(0);
                mediaPlayer.setPlayWhenReady(false);
                App.audioManager.abandonAudioFocus(i -> mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN));
                System.exit(2);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                String action = mediaButtonEvent.getAction();
                if (action != null && action.equalsIgnoreCase("android.intent.action.MEDIA_BUTTON")) {
                    Bundle extras = mediaButtonEvent.getExtras();
                    if (extras == null) {
                        return false;
                    }
                    KeyEvent keyEvent = (KeyEvent) extras.get("android.intent.extra.KEY_EVENT");
                    if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        int keyCode = keyEvent.getKeyCode();
                        isPlaying.setValue(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ? mediaPlayer.isPlaying() : keyCode == KeyEvent.KEYCODE_MEDIA_STOP || mediaPlayer.isPlaying());
                        int state = mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_PLAYING;
                        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ? state : keyCode == KeyEvent.KEYCODE_MEDIA_STOP ? PlaybackStateCompat.STATE_PAUSED : mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED, 0, 1.0f).build());
                        mediaPlayer.seekTo(0);
                        boolean toPlay = keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ? !mediaPlayer.isPlaying() : keyCode != KeyEvent.KEYCODE_MEDIA_STOP && mediaPlayer.getPlayWhenReady();
                        if (toPlay) {
                            App.audioManager.requestAudioFocus(i -> mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        } else {
                            App.audioManager.abandonAudioFocus(i -> mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN));
                        }
                        mediaPlayer.setPlayWhenReady(toPlay);
                    }
                }
                return true;
            }
        });
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f).build());
        audioManager.requestAudioFocus(i -> mediaPlayer.setPlayWhenReady(i == AudioManager.AUDIOFOCUS_GAIN), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mediaPlayer.setPlayWhenReady(true);
    }

    private void initBroadcasts() {
        broadcasts = new MutableLiveData<>();
        for (String day : daysArray) {
            FirebaseFirestore.getInstance().collection("BroadcastSchedule").document("MainBroadcastSchedule").collection(day + "Shows").addSnapshotListener((documentSnapshot, e) -> {
                List<Broadcast> broadcastList = new ArrayList<>();
                if (documentSnapshot != null) {
                    for (QueryDocumentSnapshot document : documentSnapshot) {
                        Broadcast broadcast = document.toObject(Broadcast.class);
                        broadcast.setId(document.getId());
                        broadcastList.add(broadcast);
                    }
                    HashMap<String, List<Broadcast>> map = broadcasts.getValue();
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(day, broadcastList);
                    broadcasts.setValue(map);
                }
            });
        }
    }

    private void initAppInfo() {
        FirebaseFirestore.getInstance().collection("AppInfo").addSnapshotListener((documentSnapshots, e) -> {
            if (documentSnapshots != null) {
                for (QueryDocumentSnapshot document : documentSnapshots) {
                    Object updatedStreamingUrl = document.getData().get("updatedStreamingUrl");
                    if (updatedStreamingUrl != null) {
                        String text = (String) updatedStreamingUrl;
                        if (!text.isEmpty()) {
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString("StreamingUrl", text).apply();
                        }
                    }
                    Object updatedRecentlyPlayedUrl = document.getData().get("updatedRecentlyPlayedUrl");
                    if (updatedRecentlyPlayedUrl != null) {
                        String text = (String) updatedRecentlyPlayedUrl;
                        if (!text.isEmpty()) {
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString("RecentlyPlayedUrl", text).apply();
                        }
                    }
                }
            }
        });
    }
}
