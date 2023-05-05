package com.example.synergyaudio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerView extends ConstraintLayout {
    private Track[] trackList;
    private Track currentTrack;
    private int currentTrackId = 0;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private final TextView trackName;
    private final TextView trackAuthor;
    private final TextView currentTime;
    private final TextView trackDuration;
    private final ImageView trackCover;
    private final ImageButton playButton;
    private final SeekBar trackProgress;
    private final Handler timerHandler = new Handler();

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.player_view_layout, this, true);

        trackName = findViewById(R.id.track_name);
        trackAuthor = findViewById(R.id.track_author);
        currentTime = findViewById(R.id.current_time);
        trackDuration = findViewById(R.id.duration);
        trackCover = findViewById(R.id.track_cover);
        ImageButton prevButton = findViewById(R.id.previous_track);
        playButton = findViewById(R.id.play);
        ImageButton nextButton = findViewById(R.id.next_track);
        trackProgress = findViewById(R.id.track_progress);

        trackProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                updateOnTimer();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    stop();
                } else {
                    play();
                }
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousTrack();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                trackProgress.setMax(mediaPlayer.getDuration());
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (mediaPlayer.isPlaying()) {
////                            trackProgress.setProgress(mediaPlayer.getCurrentPosition());
//                            updateOnTimer();
                            timerHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateOnTimer();
                                }
                            }, 0);
                        }
                    }
                }, 0, 1000);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) { nextTrack(); }
        });
    }

    private void updateOnTimer() {
        String currentDuration = new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(mediaPlayer.getCurrentPosition()));
        currentTime.setText(currentDuration);
        trackProgress.setProgress(mediaPlayer.getCurrentPosition());
    }

    public void setTracks(Track[] tracks) {
        trackList = tracks;
        currentTrack = trackList[currentTrackId];
        preparePlayer();
    }
    private void setTrackName() {
        trackName.setText(currentTrack.getName());
        trackAuthor.setText(currentTrack.getAuthor());
    }
    public void play() {
        mediaPlayer.start();
        playButton.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.pause)
        );
    }
    public void stop() {
        mediaPlayer.pause();
        playButton.setImageDrawable(
                AppCompatResources.getDrawable(getContext(), R.drawable.play)
        );
    }
    public void setTrack(int trackId) {
        currentTrackId = trackId;
        currentTrack = trackList[currentTrackId];
        mediaPlayer.reset();
        preparePlayer();
        play();
    }
    public void previousTrack() {
        if (currentTrackId > 0) {
            currentTrack = trackList[--currentTrackId];
            mediaPlayer.reset();
            preparePlayer();
            play();
        }
    }

    public void nextTrack() {
        if (currentTrackId < trackList.length - 1) {
            currentTrack = trackList[++currentTrackId];
            mediaPlayer.reset();
            preparePlayer();
            play();
        }
    }
    private void preparePlayer() {
        setTrackName();
        try {
            AssetFileDescriptor audioDescriptor = currentTrack.getDescriptor(getContext());
            if (audioDescriptor != null) {
                mediaPlayer.setDataSource(audioDescriptor.getFileDescriptor(), audioDescriptor.getStartOffset(), audioDescriptor.getLength());
                audioDescriptor.close();
            }

            Bitmap cover = currentTrack.getCover(getContext());
            trackCover.setImageBitmap(cover);

            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);

            String durationString = new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(mediaPlayer.getDuration()));
            trackDuration.setText(durationString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable("superState", super.onSaveInstanceState());
        state.putInt("progress", mediaPlayer.getCurrentPosition());
        state.putInt("trackId", currentTrackId);
        state.putBoolean("shouldPlay", mediaPlayer.isPlaying());
        mediaPlayer.reset();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
        } else {
            Bundle restoredState = (Bundle) state;
            Parcelable superState = restoredState.getParcelable("superState");
            super.onRestoreInstanceState(superState);
            mediaPlayer.reset();
            currentTrackId = restoredState.getInt("trackId");
            currentTrack = trackList[currentTrackId];
            preparePlayer();
            mediaPlayer.seekTo(restoredState.getInt("progress"));
            updateOnTimer();
            if (restoredState.getBoolean("shouldPlay")) {
                play();
            } else {
                trackProgress.setMax(mediaPlayer.getDuration());
                trackProgress.setProgress(restoredState.getInt("progress"));
            }
        }
    }
}