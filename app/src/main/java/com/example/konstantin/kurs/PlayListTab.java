package com.example.konstantin.kurs;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class PlayListTab extends Fragment {
    private ArrayList<Song> songList = new ArrayList<>();
    private SongAdapter songAdapter;

    public ArrayList<Song> getSongList(){
        return songList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.play_list,container,false);
        ListView songListView = (ListView) v.findViewById(R.id.song_list);
        songAdapter = new SongAdapter(getContext(), songList);
        songListView.setAdapter(songAdapter);
        return v;
    }

    public void notifyAdapter(){
        songAdapter.notifyDataSetChanged();
    }



}

