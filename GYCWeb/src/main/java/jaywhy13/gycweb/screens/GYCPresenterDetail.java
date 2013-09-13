package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;

import jaywhy13.gycweb.fragments.GYCDetailPageFragment;
import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterDetail extends Activity {

    /**
     * The fragment that contains the entire page basically...
     */
    private GYCDetailPageFragment detailPageFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailPageFragment = (GYCDetailPageFragment) getFragmentManager().findFragmentById(R.id.detailPageFragment);
        detailPageFragment.setPageTitle(getPageTitle());
        detailPageFragment.setPageSubTitle(getPageSubTitle());
    }

     /**
     * Returns a link to the detailPageFragment that contains the title, sub title, action area and list view
     * @return
     */
    public Fragment getDetailPageFragment() {
        return detailPageFragment;
    }


    /**
     * This is used to set the title of the page
     *
     * @return
     */
    public String getPageTitle() {
        return "Pastor Rutherford";
    }


    /**
     * This is used to set the sub title of the page
     *
     * @return
     */
    public String getPageSubTitle() {
        Resources resources = getResources();
        return resources.getString(R.string.presenter_list_page_title);
    }


}