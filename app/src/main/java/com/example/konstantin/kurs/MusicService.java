package com.example.konstantin.kurs;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

// Service class execute music playback continuously, even when the user is not directly interacting with the application
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;

    private ArrayList<Song> songs;

    private TextView songName;
    private TextView artistName;
    private ImageButton playButton;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 1000);
        }
    };

    private int currentSongPosition;

    private final IBinder musicBinder = new MusicBinder();

    private String songTitle = "";

    private static final int NOTIFICATION_ID = 1;

    private boolean shuffle = false;

    private boolean repeat = false;

    private Random random;

    public void onCreate() {
        super.onCreate();

        random = new Random();

        currentSongPosition = 0;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    public void setShuffle(MenuItem item) {
        shuffle = !shuffle;
        if (shuffle){
            Toast.makeText(this,"shuffle on",Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.rand_on);
        } else {
            Toast.makeText(this,"shuffle off",Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.rand);
        }
    }

    public void initMediaPlayer() {
        // The wake lock will let playback continue when device becomes idle
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnPreparedListener(MusicService.this);
                changeArtistData(0);
            }
        });
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
        ContentResolver musicResolver = getContentResolver() ;
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()){
            // get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dateIndex = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
           // int art = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            // add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisDate = musicCursor.getLong(dateIndex);
               /* if (art != -1){
                    String artCover = musicCursor.getString(art);
                    System.out.println(artCover);
                }*/
                songs.add(new Song(thisId, thisTitle, thisArtist,thisDate));
            }
            while (musicCursor.moveToNext());
        }
    }

    public void setPlayerTab(PlayerTab playerTab) {
        View view = playerTab.getView();
        this.playButton = view.findViewById(R.id.playButton);
        this.songName = view.findViewById(R.id.song_name);
        songName.setSelected(true);
        this.artistName = view.findViewById(R.id.artist_name);
        artistName.setSelected(true);
        this.seekBar = view.findViewById(R.id.progressBar);
        if (!songs.isEmpty()) {
            mediaPlayer.reset();
            Song song = songs.get(0);
            songTitle = song.getTitle();
            long songId = song.getID();
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception ex) {
                Log.e("MUSIC SERVICE", "Error setting data source", ex);
            }
            mediaPlayer.prepareAsync();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        mediaPlayer.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    public void setRepeat(MenuItem item) {
        repeat = !repeat;
        if (repeat){
            item.setIcon(R.drawable.repeat_on);
        } else {
            item.setIcon(R.drawable.repeat_ic);
        }
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        mediaPlayer.reset();

        Song song = songs.get(currentSongPosition);
        songTitle = song.getTitle();
        long songId = song.getID();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
       // MediaStore.Audio.Artists.Albums.ALBUM_ART
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception ex) {
            Log.e("MUSIC SERVICE", "Error setting data source", ex);
        }
        songName.setText(songTitle);
        artistName.setText(song.getArtist());
        playButton.setImageResource(R.drawable.media_pause);
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
    }

    public void setCurrentSongPosition(int songPosition) {
        currentSongPosition = songPosition;
        changeArtistData(songPosition);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mediaPlayer.pause();
        handler.removeCallbacks(mUpdateSeekbar);
        playButton.setImageResource(R.drawable.med_play);
    }

    public void seek(int position) {
        mediaPlayer.seekTo(position);
    }

    public void go() {
        mediaPlayer.start();
        handler.postDelayed(mUpdateSeekbar, 0);
        playButton.setImageResource(R.drawable.media_pause);
    }

    public void playPrev() {
        if (--currentSongPosition < 0) currentSongPosition = songs.size() - 1;
        changeArtistData(currentSongPosition);
        playSong();
    }

    public void playNext() {
        if (shuffle) {
            int newSongPosition = currentSongPosition;
            while (newSongPosition == currentSongPosition) {
                newSongPosition = random.nextInt(songs.size());
            }

            currentSongPosition = newSongPosition;
        } else {
            if (++currentSongPosition >= songs.size()) currentSongPosition = 0;
        }
        playSong();
    }

    private void changeArtistData(int songPosition) {
        Song song = songs.get(songPosition);
        songName.setText(song.getTitle());
        artistName.setText(song.getArtist());
        seekBar.setMax(mediaPlayer.getDuration());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer.getCurrentPosition() > 0) {
            if (repeat){
                playSong();
            } else {
                playNext();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        seekBar.setMax(mediaPlayer.getDuration());
        mp.start();
        handler.postDelayed(mUpdateSeekbar, 0);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // PendingItent take the user back to the main Activity when they select the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
    }
}
