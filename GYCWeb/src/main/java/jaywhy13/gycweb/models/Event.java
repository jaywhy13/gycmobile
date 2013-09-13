package jaywhy13.gycweb.models;

/**
 * Created by jay on 9/12/13.
 */
public class Event extends Model {

    public Event(int id, String title){
        this.setId(id);
        this.setString("title", title);
    }

    public String toString(){
        return this.getString("title");
    }
}

