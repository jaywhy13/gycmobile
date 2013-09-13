package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.os.Bundle;

import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/9/13.
 */
public class GYCThemeList extends GYCPresenterList {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getPageTitle() {
        return getResources().getString(R.string.theme_detail_page_title);
    }
}