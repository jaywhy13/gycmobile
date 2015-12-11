package jaywhy13.gycweb;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import jaywhy13.gycweb.adapters.MainMenuAdapter;
import jaywhy13.gycweb.fragments.GYCListPageFragment;
import jaywhy13.gycweb.screens.GYCEventList;
import jaywhy13.gycweb.screens.GYCMenuable;
import jaywhy13.gycweb.screens.GYCPresenterList;
import jaywhy13.gycweb.screens.GYCSermonList;
import jaywhy13.gycweb.screens.TestScreen;

public class GYCMainActivity extends Activity implements GYCMenuable {

    public static final String TAG = "jaywhy13.gycweb";

    protected GYCListPageFragment mainPageFragment;
    protected ListView pageListView;


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

        Log.d(GYCMainActivity.TAG, "onCreate called, gonna setup page list");
        setupPageList();
        overridePendingTransition(R.anim.activity_open_slide, R.anim.activity_close_shrink);
    }


    /**
     * Sets up the menu
     */
    protected void setupPageList() {
        pageListView = (ListView) findViewById(R.id.pageList);
        final String menuHeadings[] = getMenuHeadings();
        final int menuIcons[] = getMenuIcons();
        final int backgroundColors [] = getMenuBackgroundColors();
        final int [] backgroundMenuResources = getBackgroundResources();
        MainMenuAdapter adapter = new MainMenuAdapter(this, R.layout.main_menu_item,
                menuHeadings, menuIcons, backgroundColors, backgroundMenuResources);
        pageListView.setAdapter(adapter);
        ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String menuCaption = menuHeadings[i];
                menuItemClicked(i, menuCaption);
            }
        };
        pageListView.setOnItemClickListener(listener);
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
        } else if(position == 3){
            Log.d(TAG, "Opening test screen");
            activityClass = TestScreen.class;
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            Log.d(TAG, "Starting activity: " + activityClass);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
        }
    }

    @Override
    public int[] getMenuBackgroundColors() {
        return null;
    }

    @Override
    public int[] getBackgroundResources() {
        return new int[] {
                R.drawable.sermon_item, R.drawable.presenter_item, R.drawable.theme_item
        };
    }

    public GYCListPageFragment getMainPageFragment() {
        return mainPageFragment;
    }

    public void testProvider(View view){
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK & event.getRepeatCount() == 0){
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
