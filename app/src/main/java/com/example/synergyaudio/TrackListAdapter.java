package com.example.synergyaudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackViewHolder> {
    private final Track[] tracks;
    private final Context context;
    public int selectedTrackId = 0;

    private final TrackChangeListener changeListener;

    public TrackListAdapter(Context context, Track[] tracks, TrackChangeListener trackChangeListener) {
        this.tracks = tracks;
        this.context = context;
        this.changeListener = trackChangeListener;
    }
    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        public final ImageView trackCover;
        public final TextView trackName;
        public final TextView trackAuthor;
        public final VideoView playingVideo;

        public TrackViewHolder(View view) {
            super(view);

            trackCover = view.findViewById(R.id.track_cover);
            trackName = view.findViewById(R.id.track_name);
            trackAuthor = view.findViewById(R.id.track_author);
            playingVideo = view.findViewById(R.id.playing_video);
        }
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_miniature, parent, false);

        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        if (position == selectedTrackId) {
            holder.playingVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            holder.playingVideo.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.play2));
            holder.playingVideo.setVisibility(View.VISIBLE);
            holder.playingVideo.start();
            ((ConstraintLayout) holder.trackCover.getParent()).setBackground(AppCompatResources.getDrawable(context, R.drawable.selected_track));
            holder.trackName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.trackAuthor.setTextColor(Color.parseColor("#BBFFFFFF"));
        } else {
            holder.playingVideo.setVisibility(View.INVISIBLE);
            ((ConstraintLayout) holder.trackCover.getParent()).setBackgroundColor(Color.TRANSPARENT);
            holder.trackName.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
            holder.trackAuthor.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
        }
        Track currentTrack = tracks[position];
        holder.trackName.setText(currentTrack.getName());
        holder.trackAuthor.setText(currentTrack.getAuthor());
        Bitmap cover = currentTrack.getCover(context);
        holder.trackCover.setImageBitmap(cover);
        ((ConstraintLayout) holder.trackCover.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTrack(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.length;
    }

    private void changeTrack(int newTrackId) {
        int temp = selectedTrackId;
        selectedTrackId = newTrackId;
        notifyItemChanged(selectedTrackId);
        notifyItemChanged(temp);
        changeListener.onTrackChange(newTrackId);
    }

    public void setCurrentTrack(int trackId) {
        int temp = selectedTrackId;
        selectedTrackId = trackId;
        notifyItemChanged(selectedTrackId);
        notifyItemChanged(temp);
    }
}
