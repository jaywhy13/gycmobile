package jaywhy13.gycweb;

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

        Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show();

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
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d(TAG, "Starting to test our providers");
                ContentValues values = new ContentValues();
                values.put(GYCProviderMetadata.PresenterTableMetadata.PRESENTER_NAME, "Jeremy Taylor");
                values.put(GYCProviderMetadata.PresenterTableMetadata.PRESENTER_NUM_SERIES, 25);
                values.put(GYCProviderMetadata.PresenterTableMetadata.PRESENTER_NUM_SERMONS, 45);
                getContentResolver().insert(GYCProviderMetadata.PresenterTableMetadata.CONTENT_URI, values);

                // Test retrieval...
                Cursor cursor = getContentResolver().query(GYCProviderMetadata.PresenterTableMetadata.CONTENT_URI, new String[] {GYCProviderMetadata.PresenterTableMetadata.PRESENTER_NAME}, null, null, null);
                for(cursor.moveToFirst() ; !cursor.isAfterLast(); cursor.moveToNext()){
                    int nameIndex = cursor.getColumnIndex(GYCProviderMetadata.PresenterTableMetadata.PRESENTER_NAME);
                    String presenterName = cursor.getString(nameIndex);
                    Log.d(TAG, "Found a presenter: " + presenterName);
                }
                return "Done";
            }
        };

        SyncTask sTask = new SyncTask();
        sTask.execute();
    }

}
