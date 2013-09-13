package jaywhy13.gycweb.models;

/**
 * Created by jay on 9/12/13.
 */
public class Sermon extends Model {

    private final String title;

    private String slug;
    private String conference;
    private String duration;
    private String date;
    private Integer eventId;
    private Integer presenterId;

    public Sermon(int id, String title){
        this.setId(id);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(int presenterId) {
        this.presenterId = presenterId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        return this.getTitle();
    }
}
