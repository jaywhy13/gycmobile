package jaywhy13.gycweb.media;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/4/13.
 */
public class GYCMedia extends Service implements MediaPlayer.OnPreparedListener {

    MediaPlayer mediaPlayer;

    public static final String ACTION_LOAD = GYCMainActivity.TAG + ".LOAD";
    public static final String ACTION_PLAY = GYCMainActivity.TAG + ".PLAY";

    public static final String ACTION_PAUSE = GYCMainActivity.TAG + ".PAUSE";
    public static final String ACTION_STOP = GYCMainActivity.TAG + ".STOP";
    public static final String ACTION_NEXT = GYCMainActivity.TAG + ".NEXT";
    public static final String ACTION_PREVIOUS = GYCMainActivity.TAG + ".PREVIOUS";

    public static final String INTENT_SERMON_TITLE = "sermon_title";
    public static final String INTENT_SERMON_ID = "sermon_id";
    public static final String INTENT_SERMON_PRESENTER = "presenter";


    public static final String STATE_LOADING = "loading";
    public static final String STATE_PLAYING = "playing";
    public static final String STATE_PAUSED = "paused";
    public static final String STATE_STOPPED = "stopped";

    private String playerState = STATE_STOPPED;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void setPlayerState(String playerState) {
        this.playerState = playerState;
    }

    public String getPlayerState() {
        return playerState;
    }

    Sermon currentSermon;

    private GYCMediaPlayer binder = new GYCMediaPlayer();

    private boolean initialized = false;

    private void initializeMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Adds the music sidebar to an activity
     * @param mediaPlayer
     * @param activity
     * @param rootView
     */
    public static void addMusicSidebar(final GYCMediaPlayer mediaPlayer, Context activity, ViewGroup rootView){

                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View sideBar = inflater.inflate(R.layout.fragment_music_sidebar, null);
                if(sideBar != null){
                    rootView.addView(sideBar);

                    // Setup the buttons properly plz...
                    ImageView playPauseBtn = (ImageView) sideBar.findViewById(R.id.play_pause_btn);
                    ImageView stopBtn = (ImageView) sideBar.findViewById(R.id.stop_btn);
                    ImageView nextBtn = (ImageView) sideBar.findViewById(R.id.next_btn);
                    ImageView prevBtn = (ImageView) sideBar.findViewById(R.id.prev_btn);

                    playPauseBtn.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.pause_icon));

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = view.getId();
                            switch(id){
                                case R.id.play_pause_btn:
                                    if(mediaPlayer.isPaused()){
                                        mediaPlayer.resume();
                                    } else {
                                        mediaPlayer.pause();
                                    }
                                    break;
                                case R.id.stop_btn:
                                    mediaPlayer.stop();
                                    break;
                                case R.id.next_btn:
                                    break;
                                case R.id.prev_btn:
                                    break;
                            }
                        }
                    };

                    playPauseBtn.setOnClickListener(listener);
                    stopBtn.setOnClickListener(listener);
                    nextBtn.setOnClickListener(listener);
                    prevBtn.setOnClickListener(listener);
                }

    }

    /**
     * Creates a new instance of a media player
     */
    private void init(){
        if(!initialized){
            new Thread(){
                public void run(){
                    initializeMediaPlayer();
                    setInitialized(true);
                    Log.d(GYCMainActivity.TAG, "INITIALIZED MEDIA PLAYER: " + GYCMedia.this.hashCode());
                }
            }.start();
        }
    }

    /**
     * Returns an instance of the media player
     * @return
     */
    private MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }


    /**
     * Returns true if the media player is playing
     * @return
     */
    public boolean isPlaying(){
        return getPlayerState().equals(STATE_PLAYING);
        //return mediaPlayer.isPlaying();
    }

    /**
     * Stops the playback
     */
    public void stopPlayback(){
        if(isPlaying()){
            mediaPlayer.stop();
            setPlayerState(STATE_STOPPED);
            broadcastAction(ACTION_STOP);
            currentSermon = null;
        }
    }

    /**
     * Pauses the playback
     */
    public void pausePlayback(){
        mediaPlayer.pause();
        setPlayerState(STATE_PAUSED);
        broadcastAction(ACTION_PAUSE);
    }

    /**
     * Sends an intent that indicates that we're playing a track
     */
    private void broadcastAction(String action){
        Intent intent = new Intent();
        intent.setAction(action);

        if(currentSermon != null){
            ContentValues values = currentSermon.getValues();
            intent.putExtra(INTENT_SERMON_ID, currentSermon.getId());
            intent.putExtra(INTENT_SERMON_TITLE, currentSermon.getTitle());
            intent.putExtra(INTENT_SERMON_PRESENTER, currentSermon.getPresenterName());
            sendBroadcast(intent);
        }
    }

    /**
     * Resumes the playback
     */
    public void resumePlayback(){
        mediaPlayer.start();
        setPlayerState(STATE_PLAYING);
        broadcastAction(ACTION_PLAY);
    }

    public boolean isPlaybackPaused(){
        return getPlayerState().equals(STATE_PAUSED);
        //return !mediaPlayer.isPlaying() && currentSermon != null;
    }

    /**
     * Plays a sermon
     * @param sermon
     */
    public void playSermon(Context context, final Sermon sermon){

        String audioUrl = sermon.getBestUrl(context.getContentResolver());
        if(audioUrl != null){
            try {
                if(currentSermon != null || getMediaPlayer().isPlaying()){
                    getMediaPlayer().stop();
                }

                getMediaPlayer().reset(); // TODO: Should we do this??!??!?!
                getMediaPlayer().setDataSource(audioUrl);
                getMediaPlayer().prepareAsync();
                setCurrentSermon(sermon);
                setPlayerState(STATE_LOADING);
                broadcastAction(ACTION_LOAD);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // Start when ready...
        mediaPlayer.start();
        setPlayerState(STATE_PLAYING);
        broadcastAction(ACTION_PLAY);
    }

    private void setCurrentSermon(Sermon currentSermon) {
        this.currentSermon = currentSermon;
    }

    private Sermon getCurrentSermon() {
        return currentSermon;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(GYCMainActivity.TAG, "MEDIA SERVICE KILLED");
    }

    public class GYCMediaPlayer extends Binder {

        public void play(Context context, Sermon sermon){
            playSermon(context, sermon);
        }

        public void pause(){
            pausePlayback();
        }

        /**
         * Stops the current sermon from playing
         */
        public void stop(){
            if(mediaPlayer.isPlaying()){
                stopPlayback();
            }
        }

        public boolean isPaused(){
            return isPlaybackPaused();
        }

        public void resume(){
            resumePlayback();
        }

        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }

        public ArrayList<Sermon> getPlaylist(){
            return null;
        }

        public Sermon getSermon(){
            return currentSermon;
        }
    }


}
