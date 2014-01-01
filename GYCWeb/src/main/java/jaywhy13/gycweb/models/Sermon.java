package jaywhy13.gycweb.models;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.htmlcleaner.Utils;

import java.util.ArrayList;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.media.SermonDownloader;

/**
 * Created by jay on 9/12/13.
 */
public class Sermon extends Model {

    public static final String TABLE_NAME = "sermons";

    // URI and mime types
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sermons");
    public static final String CONTENT_TYPE = "vnd.android.dir/vnd.gycmobile.sermon";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.item/vnd.gycmobile.sermon";

    public static final String SERMON_TITLE = "title";
    public static final String SERMON_CONFERENCE = "conference";
    public static final String SERMON_SERIES = "series";
    public static final String SERMON_DURATION = "duration";
    public static final String SERMON_DATE = "date";
    public static final String SERMON_EVENT_ID = "event_id";
    public static final String SERMON_PRESENTER_ID = "presenter_id";
    public static final String SERMON_PRESENTER_NAME = "presenter_name";
    public static final String SERMON_SITE_URL = "site_url";
    public static final String SERMON_AUDIO_URL = "audio_url";
    public static final String SERMON_FILE_URL = "file_url";

    public Sermon(){
    }


    public Sermon(int id, String title){
        this.setId(id);
        this.setTitle(title);
    }

    public String getTitle() {
        return getString(SERMON_TITLE);
    }

    public void setTitle(String title){
        this.setString(SERMON_TITLE, title);
    }

    public void setPresenterName(String name){
        this.setString(SERMON_PRESENTER_NAME, name);
    }


    public String getPresenterName() {
        return getString(SERMON_PRESENTER_NAME);
    }


    public String getConference() {
        return getString(SERMON_CONFERENCE);
    }

    public void setConference(String conference) {
        setString(SERMON_CONFERENCE, conference);
    }

    public String getDuration() {
        return getString(SERMON_DURATION);
    }

    public void setDuration(String duration) {
        setString(SERMON_DURATION, duration);
    }

    public int getEventId() {
        return getInt(SERMON_EVENT_ID);
    }

    public void setEventId(Integer eventId) {
        setInt(SERMON_EVENT_ID, eventId);
    }

    public int getPresenterId() {
        return getInt(SERMON_PRESENTER_ID);
    }

    public void setPresenterId(Integer presenterId) {
        setInt(SERMON_PRESENTER_ID, presenterId);
    }

    public String getDate() {
        return getString(SERMON_DATE);
    }

    public void setDate(String date) {
        setString(SERMON_DATE, date);
    }

    public String toString(){
        return this.getTitle();
    }

    public void setSiteUrl(String siteUrl){
        setString(SERMON_SITE_URL, siteUrl);
    }

    public String getSiteurl(){
        return getString(SERMON_SITE_URL, "");
    }

    public void setAudioUrl(String url){
        setString(SERMON_AUDIO_URL, url);
    }

    public String getAudioUrl(){
        return getString(SERMON_AUDIO_URL, "");
    }

    public CursorLoader getOtherSermonsByPresenter(Context context){
        Sermon s = new Sermon();
        return s.getViaCursorLoader(context, Sermon.SERMON_PRESENTER_ID + "= ? AND " + BaseColumns._ID + " <> ?",
                new String[]{String.valueOf(getPresenterId()), String.valueOf(getId())});
    }

    @Override
    public String[] getFields() {
        return new String[]{
                getIdFieldName(),
                SERMON_TITLE,
                SERMON_SERIES,
                SERMON_CONFERENCE,
                SERMON_DATE,
                SERMON_DURATION,
                SERMON_AUDIO_URL,
                SERMON_SITE_URL,
                SERMON_FILE_URL,
                SERMON_PRESENTER_NAME,
                SERMON_PRESENTER_ID,
                SERMON_EVENT_ID,
                getCreatedFieldName(),
                getModifiedFieldName()
        };
    }

    @Override
    public String getFieldType(String fieldName) {
        if(SERMON_DATE.equals(fieldName)){
            return DATE_TYPE;
        } else if(SERMON_PRESENTER_ID.equals(fieldName) || SERMON_EVENT_ID.equals(fieldName)){
            return INT_TYPE;
        }
        return super.getFieldType(fieldName);
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
        return CONTENT_ITEM_TYPE;
    }

    @Override
    public String getLabelField() {
        return SERMON_TITLE;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDefaultSortOrder() {
        return SERMON_TITLE;
    }

    public void setSermonFileUrl(String fileUrl){
        setString(SERMON_FILE_URL, fileUrl);
    }

    /**
     * Returns true if the sermon if the file path is set or if the
     * sermon was downloaded using the SermonDownloader.
     * @return
     */
    public boolean isDownloaded(){
        return getString(SERMON_FILE_URL, null) != null || SermonDownloader.isSermonDownloaded(this);
    }

    public boolean isDownloading(){
        return SermonDownloader.isSermonDownloading(this);
    }

    /**
     * Blocking method, checks to ensure that we have the most up to date URI
     * @return
     */
    public String getBestUrl(ContentResolver contentResolver){
        Cursor cursor = getById(contentResolver, getId());
        cursor.moveToFirst();
        int numRows = cursor.getCount();
        Log.d(GYCMainActivity.TAG, numRows + " rows returned");
        String filePath = cursor.getString(cursor.getColumnIndex(SERMON_FILE_URL));
        String httpUrl = cursor.getString(cursor.getColumnIndex(SERMON_AUDIO_URL));
        if(filePath == null || filePath.isEmpty()){
            Log.d(GYCMainActivity.TAG, "Returning url: " + httpUrl);
            return httpUrl;
        } else {
            Log.d(GYCMainActivity.TAG, "Returning file path: " + filePath);
            return filePath;
        }
    }



    
    public String getVerboseDuration(){
        String duration = getDuration();
        String pieces [] = duration.split(":");
        if(pieces.length == 2){ // e.g. 10:12
            // only say how many mins it was ...
            return Integer.parseInt(pieces[0]) + " mins";
        } else if(pieces.length == 3){
            int hours = Integer.parseInt(pieces[0]);
            int mins = Integer.parseInt(pieces[1]);
            return (hours > 0 ? hours + " hr " : "") + mins + " mins";
        }
        return "";
    }
}
