package jaywhy13.gycweb.screens;

import android.content.Intent;
import android.os.Bundle;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterList extends GYCMainActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Intialize the main stuff...
        mainPageFragment.setPageTitle("Presenters");
    }

    @Override
    public int[] getMenuIcons() {
        return new int [] {
            R.drawable.cloud_icon, R.drawable.cloud_icon};
    }

    @Override
    public String[] getMenuHeadings() {
        return new String [] {"Dr. Schools", "Mr Joe"};
    }

    @Override
    public void menuItemClicked(int position, String caption) {
        Intent intent = new Intent(this, GYCPresenterDetail.class);
        startActivity(intent);
    }
}