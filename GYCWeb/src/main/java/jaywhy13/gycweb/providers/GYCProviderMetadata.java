package jaywhy13.gycweb.providers;

import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.net.URI;
import java.util.HashMap;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.models.Model;

/**
 * Created by jay on 9/10/13.
 */
public class GYCProviderMetadata {
    public static final String AUTHORITY = "jaywhy13.gycweb.providers.GYCProvider";

    public static TableMetadata [] metadatas = new TableMetadata[]{
        new PresenterTableMetadata(), new SermonTableMetadata()
    };



    /**
     * A map that stores a mapping from the uri match indicator to the table metadata
     */
    private static HashMap<Integer, TableMetadata> uriMatchTableMetadataMap = new HashMap<Integer,TableMetadata>();

    private GYCProviderMetadata(){} // make a private constructor

    /**
     * Takes a uriMatcher as an input and adds all the tables
     * in the metadas varible to the uriMatcher. Additionally,
     * we add a mapping from the indicator (collection and item)
     * to the table metadata object via the uriMatchTableMetadataMap
     * @param uriMatcher
     */
    public static void registerUris(UriMatcher uriMatcher){
        for(int i = 0; i < metadatas.length; i++){
            TableMetadata metadata = metadatas[i];
            String tableName = metadata.getTableName();
            // generate and setup indicators
            int collectionIndicator = i * 1;
            uriMatcher.addURI(AUTHORITY, tableName, collectionIndicator);
            uriMatchTableMetadataMap.put(collectionIndicator, metadata);

            int itemIndicator = (i * 1) + 1;
            uriMatcher.addURI(AUTHORITY, tableName + "/#", itemIndicator);
            uriMatchTableMetadataMap.put(itemIndicator, metadata);

            Log.d(GYCMainActivity.TAG, "Registering uris for " + tableName);
        }
    }

    private static void registerUri(TableMetadata metadata){


    }

    public static TableMetadata getTableMetadataForUriMatch(int match){
        if(uriMatchTableMetadataMap.containsKey(match)){
            return uriMatchTableMetadataMap.get(match);
        }
        return null;
    }

    /**
     * Creates the tables for the database
     * @param db
     */
    public static void createTables(SQLiteDatabase db){
        for(int i = 0; i < metadatas.length; i++){
            TableMetadata metadata = metadatas[i];
            String tableName = metadata.getTableName();
            String [] fields = metadata.getFields();
            String sql = getCreateTableSQL(tableName, fields);
            Log.d(GYCMainActivity.TAG, "Creating table: " + tableName + "\nSQL: " + sql);
            db.execSQL(sql);
        }
    }

    public static String getFieldDefn(String fieldName, String fieldType){
        return fieldName + " " + fieldType;
    }

    public static String getTextFieldDefn(String fieldName){
        return getFieldDefn(fieldName, "TEXT");
    }

    public static String getDateFieldDefn(String fieldName){
        return getFieldDefn(fieldName, "DATE");
    }

    public static String getPKFieldDefn(String fieldName){
        return getFieldDefn(fieldName, "INTEGER PRIMARY KEY");
    }

    public static String getIntFieldDefn(String fieldName){
        return getFieldDefn(fieldName, "INTEGER");
    }


    public static String getCreateTableSQL(String tableName, String [] fieldDefinitions){
        return "CREATE TABLE " + tableName + "( " + TextUtils.join(", ", fieldDefinitions) + " )";
    }


    public static abstract class TableMetadata implements BaseColumns {

        private TableMetadata() {} // make the constructor inaccessible

        public static final String TABLE_NAME = "presenters";

        // URI and mime types
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/presenters");
        public static final String CONTENT_TYPE = "vnd.android.dir/vnd.gycmobile.presenter";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.item/vnd.gycmobile.presenter";

        // Field names
        public static final String DEFAULT_SORT_ORDER = "name";
        public static final String CREATED_FIELD_NAME = "created";
        public static final String MODIFIED_FIELD_NAME = "modified";
        public static final String LABEL_FIELD = "name";

        public abstract String [] getFields();

        public abstract Uri getContentURI();

        public abstract String getContentType();

        public abstract String getContentItemType();

        public String getDefaultSortOrder(){
            return getModifiedFieldName() + " DESC";
        }

        public String getModifiedFieldName(){
            return MODIFIED_FIELD_NAME;
        }

        public String getCreatedFieldName(){
            return CREATED_FIELD_NAME;
        }

        public abstract String getLabelField();

        public abstract String getTableName();

    }



    public static final class PresenterTableMetadata extends TableMetadata {

        private PresenterTableMetadata() {} // make the constructor inaccessible

        public static final String TABLE_NAME = "presenters";

        // URI and mime types
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/presenters");
        public static final String CONTENT_TYPE = "vnd.android.dir/vnd.gycmobile.presenter";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.item/vnd.gycmobile.presenter";

        // Field names
        public static final String DEFAULT_SORT_ORDER = "name";
        public static final String PRESENTER_NAME = "name";
        public static final String PRESENTER_NUM_SERMONS = "num_sermons";
        public static final String PRESENTER_NUM_SERIES = "num_series";
        public static final String PRESENTER_SLUG = "slug";
        public static final String PRESENTER_CREATED = "created";
        public static final String PRESENTER_MODIFIED = "modified";


        public static final String [] FIELDS = new String[]{
                getPKFieldDefn(_ID),
                getTextFieldDefn(PRESENTER_NAME),
                getIntFieldDefn(PRESENTER_NUM_SERMONS),
                getIntFieldDefn(PRESENTER_NUM_SERIES),
                getIntFieldDefn(PRESENTER_SLUG),
                getDateFieldDefn(PRESENTER_CREATED),
                getDateFieldDefn(PRESENTER_MODIFIED),
        };

        public String [] getFields(){
            return FIELDS;
        }

        @Override
        public Uri getContentURI() {
            return CONTENT_URI;
        }

        @Override
        public String getContentType() {
            return CONTENT_TYPE;
        }

        @Override
        public String getContentItemType() {
            return CONTENT_ITEM_TYPE;
        }

        public String getDefaultSortOrder(){
            return PRESENTER_NAME;
        }

        public String getLabelField(){
            return LABEL_FIELD;
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
    }

    public static final class SermonTableMetadata extends TableMetadata {
        private SermonTableMetadata(){} // make constructor inaccessible

        public static final String TABLE_NAME = "sermons";

        public static final int INCOMING_COLLECTION_URI_INDICATOR = 2;
        public static final int INCOMING_ITEM_URI_INDICATOR = 3;

        // URI and mime types
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sermons");
        public static final String CONTENT_TYPE = "vnd.android.dir/vnd.gycmobile.sermon";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.item/vnd.gycmobile.sermon";

        // Field names
        public static final String DEFAULT_SORT_ORDER = "title";
        public static final String SERMON_TITLE = "title";
        public static final String SERMON_CONFERENCE = "conference";
        public static final String SERMON_SERIES = "series";
        public static final String SERMON_DURATION = "duration";
        public static final String SERMON_DATE = "date";
        public static final String SERMON_SLUG = "slug";

        // Foreign key links
        public static final String SERMON_EVENT_ID = "event_id";
        public static final String SERMON_PRESENTER_ID = "presenter_id";


        public static final String LABEL_FIELD = "title";

        public static final String [] FIELDS = new String [] {
                getPKFieldDefn(_ID),
                getTextFieldDefn(SERMON_TITLE),
                getTextFieldDefn(SERMON_CONFERENCE),
                getTextFieldDefn(SERMON_DURATION),
                getTextFieldDefn(SERMON_DATE),
                getTextFieldDefn(SERMON_SLUG),
                getIntFieldDefn(SERMON_PRESENTER_ID),
                getIntFieldDefn(SERMON_EVENT_ID),
        };

        public String [] getFields(){
            return FIELDS;
        }

        @Override
        public Uri getContentURI() {
            return CONTENT_URI;
        }

        @Override
        public String getContentType() {
            return CONTENT_TYPE;
        }

        @Override
        public String getContentItemType() {
            return CONTENT_ITEM_TYPE;
        }

        public String getDefaultSortOrder(){
            return SERMON_TITLE;
        }

        public String getLabelField(){
            return LABEL_FIELD;
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }


    }
}

