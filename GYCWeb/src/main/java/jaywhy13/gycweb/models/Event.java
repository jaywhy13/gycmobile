package jaywhy13.gycweb.models;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by jay on 9/12/13.
 */
public class Event extends Model {

    public static final String EVENT_TITLE = "title";
    public static final String EVENT_NUM_SERMONS = "num_sermons";
    public static final String TABLE_NAME = "events";

    // URI and mime types
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events");
    public static final String CONTENT_TYPE = "vnd.android.dir/vnd.gycmobile.event";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.item/vnd.gycmobile.event";

    public Event(){}

    public Event(int id, String title){
        this.setId(id);
        this.setString("title", title);
    }

    public void setTitle(String title){
        setString(EVENT_TITLE, title);
    }

    public String getTitle(){
        return getString(EVENT_TITLE);
    }

    public void setNumSermons(int numSermons){
        setInt(EVENT_NUM_SERMONS, numSermons);
    }

    public int getNumSermons(){
        return getInt(EVENT_NUM_SERMONS, 0);
    }

    public CursorLoader getSermons(Context context){
        Sermon s = new Sermon();
        return s.getViaCursorLoader(context, Sermon.SERMON_EVENT_ID + "= ?", new String[]{String.valueOf(getId())});
    }

    public String toString(){
        return "Event: " + this.getString("title");
    }

    @Override
    public String[] getFields() {
        return new String[]{
                getIdFieldName(),
                EVENT_TITLE,
                EVENT_NUM_SERMONS,
                getModifiedFieldName(),
                getCreatedFieldName()
        };
    }

    @Override
    public Uri getModelURI() {
        return CONTENT_URI;
    }

    @Override
    public String getModelMimeType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getContentMimeType() {
        return  CONTENT_ITEM_TYPE;
    }

    @Override
    public String getLabelField() {
        return EVENT_TITLE;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDefaultSortOrder() {
        return EVENT_TITLE;
    }
}

