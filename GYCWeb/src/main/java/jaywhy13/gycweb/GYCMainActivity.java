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
import jaywhy13.gycweb.screens.GYCSermonList;
import jaywhy13.gycweb.screens.GYCThemeList;
import jaywhy13.gycweb.tasks.SyncTask;

public class GYCMainActivity extends Activity implements GYCMenuable {

    public static final String TAG = "jaywhy13.gycweb";

    protected GYCListPageFragment mainPageFragment;


    /**
     * Returns the layout for the resource
     * @return
     */
    public int getLayoutResource(){
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mainPageFragment = (GYCListPageFragment) getFragmentManager().findFragmentById(R.id.mainPageFragment);

        // Set the title of the page...
        mainPageFragment.hidePageTitle();
        mainPageFragment.hidePageSubTitle();
        mainPageFragment.hidePageSummary();

        Log.d(GYCMainActivity.TAG, "onCreate called, gonna setup page list");
        setupPageList();

    }

    /**
     * Sets up the menu
     */
    protected void setupPageList() {
        getMainPageFragment().setMenuItemResource(R.layout.main_menu_item);
        getMainPageFragment().setupPageList();
    }

    /**
     * Returns a list of headings for the menu
     *
     * @return
     */
    public String[] getMenuHeadings() {
        return new String[]{"Sermons", "Presenters", "Events"};
    }

    /**
     * Returns a list of resource ids for the images for each menu item
     *
     * @return
     */
    public int[] getMenuIcons() {
        return new int[]{R.drawable.message_white,
                R.drawable.presenter_white,
                R.drawable.cloud_icon,
                R.drawable.cloud_icon};
    }

    @Override
    public void menuItemClicked(int position, String caption) {
        Class activityClass = null;
        if (position == 0) {
            Log.d(TAG, "Opening the sermons screen");
            activityClass = GYCSermonList.class;
        } else if (position == 1) {
            Log.d(TAG, "Opening presenters");
            activityClass = GYCPresenterList.class;
        } else if (position == 2) {
            Log.d(TAG, "Opening events");
            activityClass = GYCEventList.class;
        } else if (position == 3) {
            Log.d(TAG, "Opening blogs");
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            Log.d(TAG, "Starting activity: " + activityClass);
            startActivity(intent);
        }
    }

    @Override
    public int[] getMenuBackgroundColors() {
        return new int[] {
               0xFF00a2a5, 0xFF9400a1, 0xFFff5700
        };
    }

    public GYCListPageFragment getMainPageFragment() {
        return mainPageFragment;
    }

    public void testProvider(View view){
    }





}
