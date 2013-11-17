package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/9/13.
 */
public class GYCSermonList extends GYCPresenterList {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getPageTitle() {
        return getResources().getString(R.string.sermon_list_page_title);
    }

    @Override
    protected Cursor getCursor() {
        return new Sermon().get(getContentResolver());
    }

    @Override
    protected CursorAdapter getCursorAdapter() {
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, getCursor(), new String[]{Sermon.SERMON_TITLE, Sermon.SERMON_PRESENTER_NAME, Sermon.SERMON_DURATION},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption, R.id.menu_sub_caption_right}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }
        return sca;
    }

    @Override
    public Model getListModel() {
        return new Sermon();
    }

    @Override
    public Class getClassForListItemIntent() {
        return GYCSermonDetail.class;
    }
}