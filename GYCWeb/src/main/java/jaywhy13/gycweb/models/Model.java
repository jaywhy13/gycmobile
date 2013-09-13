package jaywhy13.gycweb.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Iterator;

/**
 * Created by jay on 9/12/13.
 */
public class Model {

    protected ContentValues data = new ContentValues();

    public static Uri create(ContentResolver contentResolver, Uri uri, ContentValues values){
        contentResolver.insert(uri, values);
        return null;
    }

    public static Cursor get(ContentResolver contentResolver, Uri uri){
        return null;
    }

    public final int getId() {
        return getInt(BaseColumns._ID);
    }

    public final void setId(int id){
        setInt(BaseColumns._ID, id);
    }


    public ContentValues getValues(){
        return data;
    }

    public void setString(String key, String value){
        this.data.put(key, value);
    }

    public void setInt(String key, Integer value){
        this.data.put(key, value);
    }

    public String getString(String key){
        return this.data.getAsString(key);
    }

    public int getInt(String key){
        return this.data.getAsInteger(key);
    }

    public void update(Model model) {
        // Get the id first and check against ours..
        ContentValues values = model.getValues();
        if(!values.containsKey(BaseColumns._ID) || values.getAsInteger(BaseColumns._ID) != getId()){
            return;
        }
        this.data.putAll(values);
    }

}
