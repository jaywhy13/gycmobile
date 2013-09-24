package jaywhy13.gycweb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import jaywhy13.gycweb.fragments.GYCListPageFragment;
import jaywhy13.gycweb.providers.GYCProviderMetadata;
import jaywhy13.gycweb.screens.GYCEventList;
import jaywhy13.gycweb.screens.GYCMenuable;
import jaywhy13.gycweb.screens.GYCPresenterList;
import jaywhy13.gycweb.screens.GYCThemeList;
import jaywhy13.gycweb.tasks.SyncTask;

public class GYCMainActivity extends Activity implements GYCMenuable {

    public static final String TAG = "jaywhy13.gycweb";

    protected GYCListPageFragment mainPageFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPageFragment = (GYCListPageFragment) getFragmentManager().findFragmentById(R.id.mainPageFragment);

        // Set the title of the page...
        mainPageFragment.setPageTitle("Main Page");
        mainPageFragment.setPageSubTitle("");
        mainPageFragment.setPageSummary("");

        // check if the first sync is complete
        if(!SyncTask.isFirstSyncComplete(this)){

            // Show the loading dialog
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.sync_message));

            // create a new sync task...
            SyncTask syncTask = new SyncTask(this){
                @Override
                protected void onPostExecute(Object o) {
                    dialog.dismiss();
                }
            };

            // Start the task and show the loading dialog...
            dialog.show();
            syncTask.execute();
        }

//        GYCMedia.addMusicSideBar(getFragmentManager(), R.id.mainPageView);
    }

    /**
     * Returns a list of headings for the menu
     *
     * @return
     */
    public String[] getMenuHeadings() {
        return new String[]{"Presenters", "Events", "Themes", "Blogs"};
    }

    /**
     * Returns a list of resource ids for the images for each menu item
     *
     * @return
     */
    public int[] getMenuIcons() {
        return new int[]{R.drawable.cloud_icon,
                R.drawable.cloud_icon,
                R.drawable.cloud_icon,
                R.drawable.cloud_icon};
    }

    @Override
    public void menuItemClicked(int position, String caption) {
        Class activityClass = null;
        Log.d(TAG, "The position that was clicked was: " + position);
        if (position == 0) {
            Log.d(TAG, "Opening the presenters screen");
            activityClass = GYCPresenterList.class;
        } else if (position == 1) {
            Log.d(TAG, "Opening events");
            activityClass = GYCEventList.class;
        } else if (position == 2) {
            Log.d(TAG, "Opening themes");
            activityClass = GYCThemeList.class;
        } else if (position == 3) {
            Log.d(TAG, "Opening blogs");
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            Log.d(TAG, "Starting activity: " + activityClass);
            startActivity(intent);
        }
    }

    public GYCListPageFragment getMainPageFragment() {
        return mainPageFragment;
    }

    public void testProvider(View view){
    }

}
