package jaywhy13.gycweb.models;


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

    public Presenter(int id, String name){
        this.setId(id);
        this.setString(PRESENTER_NAME, name);
    }


    public String getName() {
        return getString(PRESENTER_NAME);
    }

    public int getNumSermons() {
        return getInt(PRESENTER_NUM_SERMONS);
    }

    public int getNumSeries() {
        return getInt(PRESENTER_NUM_SERIES);
    }

    public String getSlug() {
        return getString(PRESENTER_SLUG);
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
        return "Presenter: " + this.getName();
    }
}
