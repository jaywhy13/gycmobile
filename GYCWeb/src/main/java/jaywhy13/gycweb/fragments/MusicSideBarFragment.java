package jaywhy13.gycweb.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/3/13.
 */
public class MusicSideBarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_sidebar, container, false);
        return view;
    }
}