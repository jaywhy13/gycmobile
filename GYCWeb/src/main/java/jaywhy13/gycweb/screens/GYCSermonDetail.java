package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by jay on 9/9/13.
 */
public class GYCSermonDetail extends GYCPresenterDetail {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getPageTitle() {
        return "Just friends";
    }

    @Override
    public String getPageSubTitle() {
        return "Sis E. Parker";
    }
}