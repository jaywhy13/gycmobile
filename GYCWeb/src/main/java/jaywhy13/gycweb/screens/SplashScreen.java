package jaywhy13.gycweb.screens;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.media.GYCMedia;
import jaywhy13.gycweb.screens.util.SystemUiHider;
import jaywhy13.gycweb.tasks.InitTask;
import jaywhy13.gycweb.tasks.SyncTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Intent mediaPlayerServiceIntent = new Intent(this, GYCMedia.class);
        startService(mediaPlayerServiceIntent);

        SyncTask syncTask = new SyncTask(this){
            @Override
            protected void onPostExecute(Object o) {
                Intent intent = new Intent(SplashScreen.this, GYCMainActivity.class);
                finish();
                startActivity(intent);
            }
        };
        syncTask.execute();

    }
}