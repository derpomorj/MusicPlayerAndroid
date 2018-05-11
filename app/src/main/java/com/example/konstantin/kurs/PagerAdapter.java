package com.example.konstantin.kurs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private final PlayListTab playlistTab;

    private final PlayerTab playerTab;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        playlistTab = new PlayListTab();
        playerTab = new PlayerTab();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return playlistTab;
        } else {
            return playerTab;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
