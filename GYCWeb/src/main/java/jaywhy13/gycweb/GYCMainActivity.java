package jaywhy13.gycweb;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import jaywhy13.gycweb.fragments.GYCListPageFragment;
import jaywhy13.gycweb.screens.GYCMenuable;
import jaywhy13.gycweb.screens.GYCPresenterList;

public class GYCMainActivity extends Activity implements GYCMenuable {

    public static final String TAG = "GYCMainActivity";

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
        Log.d(TAG, "Position clicked was: " + position);
        if (position == 0) {
            Log.d(TAG, "Opening presenters");
            activityClass = GYCPresenterList.class;
        } else if (position == 1) {
            Log.d(TAG, "Opening events");
        } else if (position == 2) {
            Log.d(TAG, "Opening themes");
        } else if (position == 3) {
            Log.d(TAG, "Opening blogs");
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }
    }

    public GYCListPageFragment getMainPageFragment() {
        return mainPageFragment;
    }

}
