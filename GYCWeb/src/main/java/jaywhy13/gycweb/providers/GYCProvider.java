package jaywhy13.gycweb.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.models.Event;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;

/**
 * The GYCProvider will be the content provider for all the different database types
 * in the database. That is, this content provider will use a UriMatcher to setup URI's
 * for presenters, sermons, events, themes and blogs.
 *
 * Created by jay on 9/10/13.
 */
public class GYCProvider extends ContentProvider {

    /**
     * Stores the list of models that will be accessible via the ContentProvider
     */
    public static Model[] models = new Model [] {
            new Presenter(), new Event(), new Sermon()
    };

    /**
     * Keeps track of the model uri's
     */
    private static ArrayList<ModelUri> modelUris = new ArrayList<ModelUri>();


    /**
     * A uri matcher for storing all the content type URI's
     */
    private static UriMatcher uriMatcher;

    /**
     * A database helper object to aid us in talking to the database
     */
    private GYCDatabaseHelper databaseHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add the uri's for presenters, sermons, etc...
        Log.d(GYCMainActivity.TAG, "Registering uris");

        /**
         * What we do here is that we used the "index" in the ArrayList as the "match"
         * number for the URI
         */
        for(Model model : models){
            ArrayList<ModelUri> uris = model.getUrls();
            for(ModelUri modelUri : uris){
                int index = modelUris.size();
                String uri = modelUri.getUri();
                uriMatcher.addURI(Model.AUTHORITY, modelUri.getUri(), index);
                modelUris.add(modelUri);
            }
        }
    }

    public static Model getModelForUri(Uri uri){
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            return null;
        }
        return modelUris.get(match).getModel();
    }

    /**
     * Returns the mime type for the given uri (this mime type is either for the collection or for an item)
     * @param uri
     * @return
     */
    public static String getMimeTypeForUri(Uri uri){
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            return null;
        }
        return modelUris.get(match).getMimeType();
    }

    /**
     * This class is used to help us talk to the database. It provides us with
     * a getReadableDatabase and getWriteableDatabase which we can use to get
     * a reference to our database and use a SQLQueryBuilder object to get back
     * a cursor or manipulate data in the database.
     */
    private static class GYCDatabaseHelper extends SQLiteOpenHelper {

        GYCDatabaseHelper(Context context){
            // Supply the ocntext to the constructor and the database name and so on...
            super(context, Model.DATABASE_NAME, null, Model.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            for(Model model : models){
                String sql = model.getCreateSQL();
                Log.d(GYCMainActivity.TAG, "Creating table for " + model.getTableName() + " with SQL: " +  sql);
                sqLiteDatabase.execSQL(sql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int v1, int v2) {
            // TODO: Drop and recreate here
        }
    }


    /**
     * Creation of the GYCProvider
     * @return
     */
    @Override
    public boolean onCreate() {
        // Create the database helper and save the context
        databaseHelper = new GYCDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Model model = getModelForUri(uri);
        if(model == null){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }

        String tableName = model.getTableName();
        String defaultSortOrder = model.getDefaultSortOrder();

        if(sortOrder == null){
            sortOrder = defaultSortOrder;
        }

        // Build a query builder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = getMimeTypeForUri(uri);
        if(mimeType == null){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }

        Model model = getModelForUri(uri);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(contentValues == null){
            contentValues = new ContentValues();
        }

        Long now = System.currentTimeMillis();
        contentValues.put(model.getCreatedFieldName(), now);
        contentValues.put(model.getModifiedFieldName(), now);

        long rowId = db.insert(model.getTableName(), model.getLabelField(), contentValues);
        return ContentUris.withAppendedId(model.getModelURI(), rowId);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = uriMatcher.match(uri);

        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        ModelUri modelUri = modelUris.get(match);

        Model model = getModelForUri(uri);

        int count = 0;

        Log.d(GYCMainActivity.TAG, "Deleting from uri: " + uri + ", details: " + modelUri);


        String tableName = model.getTableName();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(modelUri.isCollection()){
            // delete the entire collection
            count = db.delete(tableName, where, whereArgs);
        } else {
            // delete a specific one
            String rowId = uri.getPathSegments().get(1);
            count = db.delete(tableName,
                    model.getIdFieldName() + " = " + rowId + (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")"), whereArgs);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }

        ModelUri modelUri = modelUris.get(match);
        Model model = getModelForUri(uri);

        int count = 0;

        String tableName = model.getTableName();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(modelUri.isCollection()){
            // updateValues a subset of the collection
            count = db.update(tableName, contentValues, where, whereArgs);
        } else {
            // delete a specific one
            String rowId = uri.getPathSegments().get(1);
            count = db.update(tableName, contentValues,
                    model.getIdFieldName() + " = " + rowId + (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")"), whereArgs);
        }
        return count;
    }


    /**
     * An encapsulation for the Model's URI's
     */
    public static class ModelUri {
        private String uri;
        private String mimeType;
        private boolean collection = false;
        private Model model;

        public ModelUri(Model model, String uri, String mimeType, boolean collection){
            this.model = model;
            this.uri = uri;
            this.mimeType = mimeType;
            this.collection = collection;
        }

        public ModelUri(Model model, String uri, String mimeType){
            this(model, uri, mimeType, false);
        }

        public String getUri() {
            return uri;
        }

        public String getMimeType() {
            return mimeType;
        }

        public Model getModel() {
            return model;
        }

        public boolean isCollection() {
            return collection;
        }

        @Override
        public String toString() {
            return "Uri: " + uri + " for table: " + model.getTableName() + ( collection ? " [collection] " :  "");
        }
    }
}
