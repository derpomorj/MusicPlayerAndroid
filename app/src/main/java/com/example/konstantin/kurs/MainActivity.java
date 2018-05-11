package com.example.konstantin.kurs;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private ViewPager mViewPager;
    private PlayerTab playerTab;
    private MusicService musicService;
    private Intent playIntent;
    private ArrayList<Song> songList;
    private boolean paused = true;
    private boolean musicBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(songList);
            musicService.setPlayerTab(playerTab);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    private PlayListTab playListTab;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        playerTab = (PlayerTab) pagerAdapter.getItem(1);
        playListTab = (PlayListTab) pagerAdapter.getItem(0);
        PlayListTab playListTab = (PlayListTab) pagerAdapter.getItem(0);
        songList = playListTab.getSongList();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onSongPicked(View view){
        SongAdapter.ViewHolder tag = (SongAdapter.ViewHolder) view.getTag();
        int songPosition =  tag.position;
        musicService.setCurrentSongPosition(songPosition);
        paused = false;
        musicService.playSong();
        mViewPager.setCurrentItem(1);
    }



    public void onNextClicked(View view){
        playNext();
    }

    public void onPrevClicked(View view){
        playPrev();
    }

    public void onPlayClicked(View view){
        if (paused){
            musicService.go();
            paused = false;
        } else {
            pause();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    protected void onDestroy(){
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_shuffle:
                musicService.setShuffle(item);
                break;
            case R.id.action_repeat:
                musicService.setRepeat(item);
                break;
            case R.id.sort:
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Comparator<Song> c = new Comparator<Song>() {
                            @Override
                            public int compare(Song o1, Song o2) {
                                return o1.getTitle().compareTo(o2.getTitle());
                            }
                        };
                        switch (which) {
                            case 1:
                                c = new Comparator<Song>() {
                                    @Override
                                    public int compare(Song o1, Song o2) {
                                        return o1.getArtist().compareTo(o2.getArtist());
                                    }
                                };
                                break;
                            case 2:
                                c = new Comparator<Song>() {
                                    @Override
                                    public int compare(Song o1, Song o2) {
                                        return -Long.compare(o1.getDate(), o2.getDate());
                                    }
                                };
                                break;
                        }
                        Collections.sort(songList, c);
                        playListTab.notifyAdapter();
                    }
                };
                builder.setTitle("Выберите тип сортировки")
                        .setItems(R.array.sort_types,onClickListener);
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.exit:
                stopService(playIntent);
                musicService = null;
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void pause() {
        paused = true;
        musicService.pausePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  if (paused){
            paused = false;
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    //    paused = true;
    }


    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPlaying()){
            return musicService.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound && musicService.isPlaying()){
            return musicService.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicService != null && musicBound){
            return musicService.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void playNext() {
        musicService.playNext();
        if (paused) {
            paused = false;
        }
    }
    private void playPrev(){
        musicService.playPrev();
        if (paused){
            paused = false;
        }
    }

    @Override
    public void onBackPressed(){
        if (mViewPager.getCurrentItem() == 1){
            mViewPager.setCurrentItem(0);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }
}


