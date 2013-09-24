package jaywhy13.gycweb.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import jaywhy13.gycweb.GYCMainActivity;


/**
 * Created by jay on 9/12/13.
 */
public abstract class Model {

    public static final String AUTHORITY = "jaywhy13.gycweb.providers.GYCProvider";

    public static final String DATABASE_NAME = "gycmobile-test.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATED_FIELD_NAME = "created";
    public static final String MODIFIED_FIELD_NAME = "modified";

    public static final String DATE_TYPE = "DATE";
    public static final String STRING_TYPE = "TEXT";
    public static final String INT_TYPE = "INTEGER";

    protected ContentValues data = new ContentValues();

    /**
     * Inserts this object into the database
     * @param contentResolver
     * @return
     */
    public Uri insert(ContentResolver contentResolver){
        // TODO: Check if this already exists and call update instead
        Uri newUri = contentResolver.insert(getModelURI(), this.getValues());
        return newUri;
    }

    /**
     * Fetches items from the database based on the Uri given. If the URI is not supplied,
     * all items of the type will be returned since we use the model uri as the URI.
     * @param contentResolver
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param defaultOrderBy
     * @return
     */
    public Cursor get(ContentResolver contentResolver, Uri uri, String [] projection, String selection, String [] selectionArgs, String defaultOrderBy){
        if(uri == null){
            uri = getModelURI();
        }
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, defaultOrderBy);
        return cursor;
    }

    /**
     * Returns the create SQL for the table
     * @return
     */
    public String getCreateSQL(){

        String [] fields = getFields();
        ArrayList<String> fieldSQLs = new ArrayList<String>();

        for(String field : fields){
            String fieldType = getFieldType(field);
            fieldSQLs.add(field + " " + fieldType);
        }

        String sql = "CREATE TABLE " + getTableName() + "( " + TextUtils.join(", ", fieldSQLs.toArray()) + " )";
        Log.d(GYCMainActivity.TAG, " Create SQL for: " + getTableName() + ":: " + sql);
        return sql;
    }

    /**
     * Returns the name of the field type
     * @param fieldName
     * @return
     */
    public String getFieldType(String fieldName){
        if(CREATED_FIELD_NAME.equals(fieldName) || MODIFIED_FIELD_NAME.equals(fieldName)){
            return DATE_TYPE;
        }

        return STRING_TYPE;
    }

    /**
     * Fetches items from the dtabase based on the Uri
     * @param contentResolver
     * @param uri
     * @return
     */
    public Cursor get(ContentResolver contentResolver, Uri uri){
        Cursor cursor = contentResolver.query(uri, null, null, null, getDefaultSortOrder());
        return cursor;
    }

    /**
     * Get all items of this type
     * @param contentResolver
     * @return
     */
    public Cursor get(ContentResolver contentResolver){
        return get(contentResolver, null);
    }

    /**
     * Returns the ID
     * @return
     */
    public final int getId() {
        return getInt(BaseColumns._ID);
    }

    /**
     * Returns the field name for the ID column
     * @return
     */
    public final String getIdFieldName(){
        return BaseColumns._ID;
    }

    /**
     * Sets the ID
     * @param id
     */
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

    public String getString(String key, String def){
        if(this.data.containsKey(key)){
            return this.data.getAsString(key);
        }
        return def;
    }

    public String getString(String key){
        return getString(key, null);
    }

    public int getInt(String key, int def){
        if(this.data.containsKey(key)){
            return this.data.getAsInteger(key);
        }
        return def;
    }

    public int getInt(String key){
        return getInt(key, -1);
    }

    public int updateByUri(ContentResolver contentResolver, Uri uri, ContentValues values, String where, String [] selectionArgs){
        return contentResolver.update(uri, values, where, selectionArgs);
    }

    public int updateByUri(ContentResolver contentResolver, Uri uri, ContentValues values){
        return updateByUri(contentResolver, uri, values, null, null);
    }

    /**
     * Update this instance
     * @param contentResolver
     * @param values
     * @return
     */
    public int update(ContentResolver contentResolver, ContentValues values){
        return updateByUri(contentResolver, getURI(), values);
    }

    public int deleteByUri(ContentResolver contentResolver, Uri uri, String where, String [] selectionArgs){
        return contentResolver.delete(uri, where, selectionArgs);
    }

    /**
     * Delete this instance
     * @param contentResolver
     * @return
     */
    public int delete(ContentResolver contentResolver){
        return deleteByUri(contentResolver, getURI(), null, null);
    }

    public int deleteAll(ContentResolver contentResolver){
        return deleteByUri(contentResolver, getModelURI(), null, null);
    }


    public void updateValues(Model model) {
        // Get the id first and check against ours..
        ContentValues values = model.getValues();
        if(!values.containsKey(BaseColumns._ID) || values.getAsInteger(BaseColumns._ID) != getId()){
            return;
        }
        this.data.putAll(values);
    }


    public abstract String [] getFields();

    /**
     * Returns the URI for this model
     * @return
     */
    public abstract Uri getModelURI();

    /**
     * Returns the URI for this instance
     * @return
     */
    public Uri getURI(){
        return ContentUris.withAppendedId(getModelURI(), getId());
    }

    /**
     * Returns a MIME type describing the list of this model object
     * @return
     */
    public abstract String getModelMimeType();

    /**
     * Returns a MIME type for a single item of this object type
     * @return
     */
    public abstract String getContentMimeType();

    public String getDefaultSortOrder(){
        return getModifiedFieldName() + " DESC";
    }

    /**
     * The name of the field that is used to store the last modified date
     * @return
     */
    public String getModifiedFieldName(){
        return MODIFIED_FIELD_NAME;
    }

    /**
     * The name of the field that stores when the object was created
     * @return
     */
    public String getCreatedFieldName(){
        return CREATED_FIELD_NAME;
    }

    /**
     * The field that is used to label the object. This is also used as the null column hack
     * @return
     */
    public abstract String getLabelField();

    /**
     * Returns the name of the table
     * @return
     */
    public abstract String getTableName();

}
