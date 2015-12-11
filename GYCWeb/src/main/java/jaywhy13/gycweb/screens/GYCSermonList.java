package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Sermon;
import jaywhy13.gycweb.util.Utils;

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
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, null, new String[]{Sermon.SERMON_TITLE, Sermon.SERMON_PRESENTER_NAME, Sermon.SERMON_DURATION},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption, R.id.menu_sub_caption_right}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            sca.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int i) {
                    if(Sermon.SERMON_DURATION.equals(cursor.getColumnName(i))){
                        TextView textView = (TextView) view;
                        String duration = Utils.getVerboseDuration(cursor.getString(i));
                        textView.setText(duration);
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
        return new Sermon();
    }

    @Override
    public Class getClassForListItemIntent() {
        return GYCSermonDetail.class;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new Sermon().getViaCursorLoader(this);
    }

}
