package jaywhy13.gycweb.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterList extends GYCMainActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Intialize the main stuff...
        mainPageFragment.setPageTitle(getPageTitle());
        mainPageFragment.setPageSubTitle(getPageSubTitle());
    }

    public String getPageSubTitle(){
        return "";
    }

    /**
     * Returns the page title
     * @return
     */
    public String getPageTitle(){
        return getResources().getString(R.string.presenter_list_page_title);
    }

    @Override
    public int[] getMenuIcons() {
        return new int [] {
            R.drawable.cloud_icon, R.drawable.cloud_icon};
    }

    @Override
    public String[] getMenuHeadings() {
        return new String [] {"Item One", "Item Two"};
    }

    @Override
    public void menuItemClicked(int position, String caption) {
        Intent intent = new Intent(this, GYCPresenterDetail.class);
        startActivity(intent);
    }

    /**
     * Returns the page list in the fragment
     * @return
     */
    public ListView getPageList(){
        return mainPageFragment.getPageListView();
    }
}