package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Event;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/9/13.
 */
public class GYCEventDetail extends GYCPresenterDetail {

    Event event = new Event();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected CursorLoader getCursorLoader() {
        return event.getSermons(this);
    }

    @Override
    protected CursorAdapter getCursorAdapter() {
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, null, new String[]{Sermon.SERMON_TITLE, Sermon.SERMON_PRESENTER_NAME},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }
        return sca;
    }

    @Override
    public Model getModel() {
        return event;
    }

}