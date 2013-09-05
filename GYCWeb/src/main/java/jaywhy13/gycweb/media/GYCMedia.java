package jaywhy13.gycweb.media;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import jaywhy13.gycweb.MainActivity;
import jaywhy13.gycweb.MusicSideBarFragment;
import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/4/13.
 */
public class GYCMedia {
    /**
     * Adds the music sidebar if something is currently playing
     */
    public static void addMusicSideBar(FragmentManager manager, int targetView){
        if(!isStopped()){
            FragmentTransaction transaction = manager.beginTransaction();
            MusicSideBarFragment fragment = new MusicSideBarFragment();
            transaction.add(targetView, fragment, "MUSICSIDEBAR");
            transaction.commit();
            Log.d(MainActivity.TAG, "Successfully addded the music sidebar");
        }
    }

    /**
     * Returns true if a sermon is playing
     * @return
     */
    public static boolean isPlaying(){
        return true;
    }

    /**
     * Returns true if a sermon was paused...
     * @return
     */
    public static boolean isPaused(){
        return false;
    }

    /**
     * Returns true if nothing is playing
     * @return
     */
    public static boolean isStopped(){
        return false;
    }
}
