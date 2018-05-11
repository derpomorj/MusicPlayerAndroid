package com.example.konstantin.kurs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

// Map the songs list to the ListView
public class SongAdapter extends BaseAdapter{
    private ArrayList<Song> songs;
    // Map the title and artist strings to the TextView
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs = theSongs;
        songInf = LayoutInflater.from(c);
    }

    static class ViewHolder {
        TextView songTitle;
        TextView artistName;
        int position;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = (LinearLayout) songInf.inflate(R.layout.song, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.songTitle = convertView.findViewById(R.id.song_title);
            viewHolder.artistName = convertView.findViewById(R.id.song_artist);
            viewHolder.position = position;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Song currSong = songs.get(position);

        viewHolder.songTitle.setText(currSong.getTitle());
        viewHolder.artistName.setText(currSong.getArtist());

        return convertView;
    }
}

