package com.example.konstantin.kurs;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Song {
    private long id;
    private String title;
    private String artist;
    private long date;

    public Song(long songID, String songTitle, String songArtist, long dateIndex) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        date = dateIndex;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}



