package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.os.Bundle;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/9/13.
 */
public class GYCSermonDetail extends GYCPresenterDetail {

    Sermon sermon = new Sermon();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getPageTitle() {
        return sermon.getTitle();
    }

    @Override
    public String getPageSubTitle() {
        return String.valueOf(sermon.getPresenterId());
    }

    @Override
    public Model getModel() {
        return sermon;
    }
}

