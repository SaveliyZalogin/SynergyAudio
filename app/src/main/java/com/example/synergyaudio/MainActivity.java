package com.example.synergyaudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final Track[] tracks = {
            new Track("God's Plan", "Drake", "audio/gods_plan.mp3", "covers/gods_plan.jpeg"),
            new Track("Highest in the room", "Travis Scott", "audio/highest_in_the_room.mp3", "covers/highest_in_the_room.jpeg"),
            new Track("Rockstar", "Post Malone ft. 21 Savage ", "audio/rockstar.mp3", "covers/rockstar.jpeg")
    };

    private TrackListAdapter trackListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlayerView playerView = findViewById(R.id.player);
        playerView.setTracks(tracks);

        TrackChangeListener changeListener = new TrackChangeListener() {
            @Override
            public void onTrackChange(int newTrackId) {
                playerView.setTrack(newTrackId);
            }
        };

        RecyclerView trackList = findViewById(R.id.track_list);
        trackListAdapter = new TrackListAdapter(this, tracks, changeListener);
        trackList.setAdapter(trackListAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("trackId", trackListAdapter.selectedTrackId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        trackListAdapter.setCurrentTrack(savedInstanceState.getInt("trackId"));
    }
}