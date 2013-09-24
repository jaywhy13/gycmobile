package jaywhy13.gycweb.models;


import android.net.Uri;

/**
 * Created by jay on 9/12/13.
 */
public class Presenter extends Model {

    public static final String PRESENTER_NAME = "name";
    public static final String PRESENTER_NUM_SERMONS = "num_sermons";
    public static final String PRESENTER_NUM_SERIES = "num_series";
    public static final String PRESENTER_SLUG = "slug";
    public static final String PRESENTER_CREATED = "created";
    public static final String PRESENTER_MODIFIED = "modified";

    public static final String TABLE_NAME = "presenters";

    // URI and mime types
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/presenters");
    public static final String CONTENT_TYPE = "vnd.android.dir/vnd.gycmobile.presenter";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.item/vnd.gycmobile.presenter";

    public Presenter(){

    }

    public Presenter(int id, String name){
        this.setId(id);
        this.setString(PRESENTER_NAME, name);
    }


    public String getName() {
        return getString(PRESENTER_NAME, "Unknown Presenter");
    }

    public int getNumSermons() {
        return getInt(PRESENTER_NUM_SERMONS, 0);
    }

    public int getNumSeries() {
        return getInt(PRESENTER_NUM_SERIES, 0);
    }

    public String getSlug() {
        return getString(PRESENTER_SLUG, "");
    }

    public void setNumSermons(int numSermons) {
        this.setInt(PRESENTER_NUM_SERMONS, numSermons);
    }

    public void setNumSeries(int numSeries) {
        this.setInt(PRESENTER_NUM_SERIES, numSeries);
    }

    public void setSlug(String slug) {
        this.setString(PRESENTER_SLUG, slug);
    }

    public String toString(){
        return "Presenter: " + this.getName() + " found at: " + getSlug() + " has preached " + getNumSermons() + " sermons, in " + getNumSeries() + " series.";
    }

    @Override
    public String[] getFields() {
        return new String[]{
                getIdFieldName(),
                PRESENTER_NAME,
                PRESENTER_NUM_SERIES,
                PRESENTER_NUM_SERMONS,
                PRESENTER_SLUG,
                PRESENTER_CREATED,
                PRESENTER_MODIFIED
        };
    }

    public String getFieldType(String fieldName){
        if(PRESENTER_NUM_SERIES.equals(fieldName) || PRESENTER_NUM_SERMONS.equals(fieldName)){
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
        return PRESENTER_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDefaultSortOrder() {
        return PRESENTER_NAME;
    }
}
