package com.example.synergyaudio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class Track {
    private final String name;
    private final String author;
    private final String url;
    private final String coverUrl;

    public Track(String name, String author, String url, String coverUrl) {
        this.name = name;
        this.author = author;
        this.url = url;
        this.coverUrl = coverUrl;
    }

    public String getAuthor() { return this.author; }
    public String getName() { return this.name; }

    public @Nullable AssetFileDescriptor getDescriptor(Context context) {
        try {
            return context.getAssets().openFd(this.url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public @Nullable Bitmap getCover(Context context) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(this.coverUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
