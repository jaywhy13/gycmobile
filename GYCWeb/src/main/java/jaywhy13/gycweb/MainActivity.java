package jaywhy13.gycweb;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import jaywhy13.gycweb.media.GYCMedia;

public class MainActivity extends Activity {

    public static final String TAG = "GYCMainActivity";

    GYCListPageFragment mainPageFragment;

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
}
