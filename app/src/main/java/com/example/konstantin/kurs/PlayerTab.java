package com.example.konstantin.kurs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerTab extends Fragment {
    private TextView songTitle;

    private TextView artistName;

    public SeekBar getSeekBar() {
        return seekBar;
    }

    private SeekBar seekBar;

    private ImageView songPicture;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.song_changer,container,false);
        songTitle = v.findViewById(R.id.song_name);
        artistName = v.findViewById(R.id.artist_name);
        songPicture = v.findViewById(R.id.song_image);
      //  seekBar = v.findViewById(R.id.progressBar);
        return v;
    }
    public TextView getSongTitle() {
        return songTitle;
    }

    public TextView getArtistName() {
        return artistName;
    }

    public ImageView getSongPicture() {
        return songPicture;
    }
}
