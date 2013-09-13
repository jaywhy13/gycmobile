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

import jaywhy13.gycweb.GYCMainActivity;

/**
 * The GYCProvider will be the content provider for all the different database types
 * in the database. That is, this content provider will use a UriMatcher to setup URI's
 * for presenters, sermons, events, themes and blogs.
 *
 * Created by jay on 9/10/13.
 */
public class GYCProvider extends ContentProvider {

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
        GYCProviderMetadata.registerUris(uriMatcher);
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
            super(context, GYCProviderMetadata.DATABASE_NAME, null, GYCProviderMetadata.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            GYCProviderMetadata.createTables(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            Log.d(GYCMainActivity.TAG, "Nothing to upgrade");
        }
    }


    @Override
    public boolean onCreate() {
        databaseHelper = new GYCDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        GYCProviderMetadata.TableMetadata metadata = GYCProviderMetadata.getTableMetadataForUriMatch(match);

        String tableName = metadata.getTableName();
        String defaultSortOrder = metadata.getDefaultSortOrder();

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
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        GYCProviderMetadata.TableMetadata metadata = GYCProviderMetadata.getTableMetadataForUriMatch(match);
        if(match == 0 || match % 2 == 0){
            return metadata.getContentType();
        } else {
            return metadata.getContentItemType();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        GYCProviderMetadata.TableMetadata metadata = GYCProviderMetadata.getTableMetadataForUriMatch(match);
        if(match > 0 && match % 2 != 0){
            throw new IllegalArgumentException("Cannot insert into uri: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(contentValues == null){
            contentValues = new ContentValues();
        }

        Long now = System.currentTimeMillis();
        contentValues.put(metadata.getCreatedFieldName(), now);
        contentValues.put(metadata.getModifiedFieldName(), now);

        long rowId = db.insert(metadata.getTableName(), metadata.getLabelField(), contentValues);
        return ContentUris.withAppendedId(metadata.getContentURI(), rowId);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        GYCProviderMetadata.TableMetadata metadata = GYCProviderMetadata.getTableMetadataForUriMatch(match);

        int count = 0;

        String tableName = metadata.getTableName();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(match == 0 || match % 2 == 0){
            // delete the entire collection
            count = db.delete(tableName, where, whereArgs);
        } else {
            // delete a specific one
            String rowId = uri.getPathSegments().get(1);
            count = db.delete(tableName,
                    metadata._ID + " = " + rowId + (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")"), whereArgs);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        int match = uriMatcher.match(uri);
        if(match == UriMatcher.NO_MATCH){
            throw new IllegalArgumentException("Invalid URI requested: " + uri);
        }
        GYCProviderMetadata.TableMetadata metadata = GYCProviderMetadata.getTableMetadataForUriMatch(match);

        int count = 0;

        String tableName = metadata.getTableName();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(match == 0 || match % 2 == 0){
            // update a subset of the collection
            count = db.update(tableName, contentValues, where, whereArgs);
        } else {
            // delete a specific one
            String rowId = uri.getPathSegments().get(1);
            count = db.update(tableName, contentValues,
                    metadata._ID + " = " + rowId + (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")"), whereArgs);
        }
        return count;
    }
}
