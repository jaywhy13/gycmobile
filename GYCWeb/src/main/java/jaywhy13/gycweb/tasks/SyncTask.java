package jaywhy13.gycweb.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.models.Event;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;
import jaywhy13.gycweb.net.Internet;
import utils.json.JSONParser;

/**
 * This class is responsible for contacting the GYC Web server and
 * downloading all the sermons, presenters, themes and blogs
 * and insert them into our database using the content provider.
 * {
 *  "id":"5562",
 *  "name":"Human Rights and the Gospel",
 *  "slug":"human-rights-and-the-gospel",
 *  "date":"2013-03-18 19:39:11",
 *  "duration":"54:29",
 *  "photo":null,
 *  "presenter_id":"794",
 *  "event_id":"765",
 *  "series_photo":"",
 *  "presenters":{ // they either have a list of presenters or a "presenter" and "presenter_slug"
 *    "794":{
 *      "name":"Mindi Rahn",
 *      "slug":"mindi-rahn"
 *     },
 *  },
 *  "presenter":"Anil Kanda",
 *  "presenter_slug":"anil-kanda",
 * }
 *
 * Created by jay on 9/12/13.
 */
public class SyncTask extends AsyncTask {

    private HashMap<String, Presenter> presenters = new HashMap<String, Presenter>();
    private HashMap<String, Sermon> sermons = new HashMap<String, Sermon>();
    private HashMap<String, Event> events = new HashMap<String, Event>();

    public static String GYC_CONTROLLER_URL = "http://gycweb.org/sm-controller.php";
    @Override
    protected Object doInBackground(Object[] objects) {
        // First do a post with vo-action = init to get presenter list, theme list and event list
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("vo-action", "init"));
        String result = Internet.post(GYC_CONTROLLER_URL, params);
        Log.d(GYCMainActivity.TAG, "Got back JSON: " + result);
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject panel = obj.getJSONObject("panel");

            // Get presenters
            JSONObject presenterTmp = panel.getJSONObject("presenter");
            JSONObject presenterDict = presenterTmp.getJSONObject("array");
            Iterator iterator = presenterDict.keys();
            while(iterator.hasNext()){
                String presenterId = iterator.next().toString();
                JSONObject presenter = presenterDict.getJSONObject(presenterId);
                String name = presenter.getString("name");
                addPresenter(new Presenter(Integer.valueOf(presenterId), name));
            }

            // Get the events
            JSONObject eventTmp = panel.getJSONObject("event");
            JSONObject eventDict = eventTmp.getJSONObject("array");
            Iterator eventIterator = eventDict.keys();
            while(eventIterator.hasNext()){
                String eventId = eventIterator.next().toString();
                JSONObject presenter = eventDict.getJSONObject(eventId);
                String name = presenter.getString("name");
                addEvent(new Event(Integer.valueOf(eventId), name));
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        result = Internet.get(GYC_CONTROLLER_URL);
        // TODO: Change this to vo-action = null and use the limit and page in filter-conditions to scroll through
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray sermonList = obj.getJSONArray("content");
            for(int i = 0; i < sermonList.length(); i++){
                JSONObject sermonJson = sermonList.getJSONObject(i);
                int id = Integer.valueOf(sermonJson.getString("id"));
                String slug = sermonJson.getString("slug");
                String title = sermonJson.getString("name");
                String date = sermonJson.getString("date");
                String duration = sermonJson.getString("duration");

                Integer eventId = null;

                try {
                    eventId = Integer.valueOf(sermonJson.getString("event_id"));
                } catch(NumberFormatException nfe){
                    Log.d(GYCMainActivity.TAG, "No event id for: " + title);
                }

                Integer presenterId = null;
                if(sermonJson.has("presenter")){
                    String presenterSlug = sermonJson.getString("presenter_slug");
                    presenterId = Integer.valueOf(sermonJson.getString("presenter_id"));
                    updatePresenterSlug(presenterId, presenterSlug);
                } else if(sermonJson.has("presenters")){
                    presenterId = Integer.valueOf(sermonJson.getString("presenter_id"));
                    String presenterSlug = sermonJson.getJSONObject("presenters").getJSONObject(String.valueOf(presenterId)).getString("slug");
                    updatePresenterSlug(presenterId, presenterSlug);
                }


                Sermon sermon = new Sermon(id, title);
                sermon.setSlug(slug);
                sermon.setDuration(duration);
                sermon.setEventId(eventId);
                sermon.setDate(date);
                sermon.setPresenterId(presenterId);
                addSermon(sermon);

                // Add one sermon to the presenter
                addSermonToPresenter(presenterId);


                Log.d(GYCMainActivity.TAG, "Processing sermon: " + title);
            }
            Log.d(GYCMainActivity.TAG, "Processed " + sermonList.length() + " sermons");

        } catch (JSONException e) {
            Log.e(GYCMainActivity.TAG, "Error parsing JSON... sorry");
            e.printStackTrace();
        }
        return "Done";
    }


    private void addPresenter(Presenter newPresenter){
        Integer id = newPresenter.getId();
        if(presenters.containsKey(String.valueOf(id))){
            Presenter presenter = presenters.get(id);
            presenter.update(newPresenter);
            Log.d(GYCMainActivity.TAG, "Updating presenter: " + presenter);
        } else {
            presenters.put(String.valueOf(id), newPresenter);
            Log.d(GYCMainActivity.TAG, "Adding presenter: " + newPresenter);
        }
    }

    private void updatePresenterSlug(int id, String slug){
        if(presenters.containsKey(id)){
            Presenter presenter = presenters.get(id);
            presenter.setSlug(slug);
            Log.d(GYCMainActivity.TAG, "Updating presenter slug: " + presenter + " to " + slug);
        }
    }

    private void addSermonToPresenter(int id){
        if(presenters.containsKey(id)){
            Presenter presenter = presenters.get(id);
            presenter.setNumSermons(presenter.getNumSermons()+1);
            Log.d(GYCMainActivity.TAG, "Updating presenter num sermons: " + presenter + " to " + presenter.getNumSermons());
        }

    }


    private void addEvent(Event newEvent){
        Integer id = newEvent.getId();
        if(events.containsKey(String.valueOf(id))){
            Event event = events.get(id);
            event.update(newEvent);
            Log.d(GYCMainActivity.TAG, "Updating event: " + event);
        } else {
            events.put(String.valueOf(id), newEvent);
            Log.d(GYCMainActivity.TAG, "Adding event: " + newEvent);
        }
    }


    private void addSermon(Sermon sermon){
        Integer id = sermon.getId();
        sermons.put(String.valueOf(id), sermon);
        Log.d(GYCMainActivity.TAG, "Adding sermon: " + sermon);
    }



}
