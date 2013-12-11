package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Event;
import jaywhy13.gycweb.models.Model;

/**
 * Created by jay on 9/9/13.
 */
public class GYCEventList extends GYCPresenterList {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getPageTitle() {
        return getResources().getString(R.string.event_list_page_title);
    }

    @Override
    protected Cursor getCursor() {
        return new Event().get(getContentResolver());
    }

    @Override
    protected CursorAdapter getCursorAdapter() {
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, getCursor(), new String[]{Event.EVENT_TITLE, Event.EVENT_NUM_SERMONS},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            sca.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int i) {
                    if(Event.EVENT_NUM_SERMONS.equals(cursor.getColumnName(i))){
                        int numSermons = cursor.getInt(i);
                        String caption = numSermons == 1 ? "1 sermon" : numSermons + " sermons";
                        ((TextView) view).setText(caption);
                        return true;
                    }
                    return false;
                }
            });
        }
        return sca;
    }

    @Override
    public Model getListModel() {
        return new Event();
    }

    @Override
    public Class getClassForListItemIntent() {
        return GYCEventDetail.class;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new Event().getViaCursorLoader(this);
    }
}