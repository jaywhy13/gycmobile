package jaywhy13.gycweb.models;

import android.net.Uri;

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
    public static final String SERMON_SLUG = "slug";
    public static final String SERMON_EVENT_ID = "event_id";
    public static final String SERMON_PRESENTER_ID = "presenter_id";
    public static final String SERMON_SITE_URL = "site_url";
    public static final String SERMON_AUDIO_URL = "audio_url";


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

    public String getSlug() {
        return getString(SERMON_SLUG);
    }

    public void setSlug(String slug) {
        setString(SERMON_SLUG, slug);
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

    @Override
    public String[] getFields() {
        return new String[]{
                getIdFieldName(),
                SERMON_TITLE,
                SERMON_SERIES,
                SERMON_SLUG,
                SERMON_CONFERENCE,
                SERMON_DATE,
                SERMON_DURATION,
                SERMON_AUDIO_URL,
                SERMON_SITE_URL,
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
}
