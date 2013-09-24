package jaywhy13.gycweb.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Event;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;
import jaywhy13.gycweb.net.Internet;

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

    private final Context context;

    public SyncTask(Context context){
        this.context = context;
    }

    private static final String GYC_MEDIA_URL_BASE = "http://gycweb.org/media/";
    private HashMap<String, Presenter> presenters = new HashMap<String, Presenter>();
    private HashMap<String, Sermon> sermons = new HashMap<String, Sermon>();
    private HashMap<String, Event> events = new HashMap<String, Event>();

    public static final int DELAY_BETWEEN_REQUESTS = 300;

    public static String GYC_CONTROLLER_URL = "http://gycweb.org/sm-controller.php";
    @Override
    protected Object doInBackground(Object[] objects) {
        // First do a post with vo-action = init to get presenter list, theme list and event list
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("vo-action", "init"));
        String result = Internet.post(GYC_CONTROLLER_URL, params);
        int totalSermons = 0;
        Log.d(GYCMainActivity.TAG, "Got back JSON: " + result);
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject panel = obj.getJSONObject("panel");
            totalSermons = obj.getInt("resultTotal");

            // Get presenters (this listing just has the ID and name of the presenter)
            JSONObject presenterTmp = panel.getJSONObject("presenter");
            JSONObject presenterDict = presenterTmp.getJSONObject("array");
            Iterator iterator = presenterDict.keys();
            while(iterator.hasNext()){
                String presenterId = iterator.next().toString();
                JSONObject presenter = presenterDict.getJSONObject(presenterId);
                String name = presenter.getString("name");
                addPresenter(new Presenter(Integer.valueOf(presenterId), name));
            }

            // Get the events (this listing just has the ID and name of the event)
            JSONObject eventTmp = panel.getJSONObject("event");
            JSONObject eventDict = eventTmp.getJSONObject("array");
            Iterator eventIterator = eventDict.keys();
            while(eventIterator.hasNext()){
                String eventId = eventIterator.next().toString();
                JSONObject event = eventDict.getJSONObject(eventId);
                String name = event.getString("name");
                addEvent(new Event(Integer.valueOf(eventId), name));
            }

        } catch(JSONException e){
            e.printStackTrace();
        }

        // Now loop through the site page by page to get all the sermons
        // This has details about the sermon and also has the presenter slug
        int numPages = totalSermons / 100;
        for(int p = 0; p < numPages; p++){
            params.clear();
            params.add(new BasicNameValuePair("vo-action", "null"));
            params.add(new BasicNameValuePair("filter_conditions", "{\"page\": " + p + ", \"limit\": " + 100 + "}"));

            result = Internet.post(GYC_CONTROLLER_URL, params);
            Log.d(GYCMainActivity.TAG, "Parsing page: " + p);
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
                        //Log.d(GYCMainActivity.TAG, "No event id for: " + title);
                    }

                    Integer presenterId = null;
                    if(sermonJson.has("presenter")){
                        try {
                        String presenterSlug = sermonJson.getString("presenter_slug");
                        presenterId = Integer.valueOf(sermonJson.getString("presenter_id"));
                        updatePresenterSlug(presenterId, presenterSlug);
                        }
                        catch(NumberFormatException nfe){
                            //Log.d(GYCMainActivity.TAG, "Error occurred while assigning presetner for " + title);
                        }
                    } else if(sermonJson.has("presenters")){
                        try {
                            // this is the main presenter ID
                            presenterId = Integer.valueOf(sermonJson.getString("presenter_id"));
                            String presenterSlug = sermonJson.getJSONObject("presenters").getJSONObject(String.valueOf(presenterId)).getString("slug");
                            updatePresenterSlug(presenterId, presenterSlug);

                            // Now loop and update all the other presenter slugs
                            JSONObject presentersJson = sermonJson.getJSONObject("presenters");
                            Iterator<String> presenterIds = sermonJson.getJSONObject("presenters").keys();
                            while(presenterIds.hasNext()){
                                int _presenterId = Integer.valueOf(presenterIds.next());
                                if(_presenterId != presenterId){
                                    JSONObject presenterJson = presentersJson.getJSONObject(new Integer(_presenterId).toString());
                                    String _presenterSlug = presenterJson.getString("slug");
                                    updatePresenterSlug(_presenterId, _presenterSlug);
                                }
                            }
                        } catch(NumberFormatException nfe){
                            //Log.d(GYCMainActivity.TAG, "Error occurred while assigning presetner for " + title);
                        }
                    }


                    // Create the sermon and add it...
                    Sermon sermon = new Sermon(id, title);
                    sermon.setSlug(slug);
                    sermon.setDuration(duration);
                    sermon.setEventId(eventId);
                    sermon.setDate(date);
                    String siteUrl = GYC_MEDIA_URL_BASE + slug;
                    sermon.setSiteUrl(siteUrl);

                    if(presenterId != null){
                        sermon.setPresenterId(presenterId);
                        addSermonToPresenter(presenterId);
                    }
                    addSermon(sermon);
                }
                Log.d(GYCMainActivity.TAG, "Processed " + sermonList.length() + " sermons");

            } catch (JSONException e) {
                Log.e(GYCMainActivity.TAG, "Error parsing JSON... sorry");
                e.printStackTrace();
            }

            try {
                Thread.sleep(DELAY_BETWEEN_REQUESTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(GYCMainActivity.TAG, "Sync is complete");

        // Now to do the database insertions...
        // Add the events to the database ...
        // Clear out the events...
        new Event().deleteAll(context.getContentResolver());
        Iterator<String> eventIdsIterator = events.keySet().iterator();
        while(eventIdsIterator.hasNext()){
            String id = eventIdsIterator.next();
            Event event = events.get(id);
            event.insert(context.getContentResolver());
        }

        new Presenter().deleteAll(context.getContentResolver());
        // Add the presenters to the database
        Iterator<String> presenterIdsIterator = presenters.keySet().iterator();
        while(presenterIdsIterator.hasNext()){
            String id = presenterIdsIterator.next();
            Presenter presenter = presenters.get(id);
            Log.d(GYCMainActivity.TAG, "Adding presenter to database: " + presenter);
            presenter.insert(context.getContentResolver());
        }

        return "Done";
    }


    private void addPresenter(Presenter newPresenter){
        Integer id = newPresenter.getId();
        if(presenters.containsKey(String.valueOf(id))){
            Presenter presenter = presenters.get(id);
            presenter.updateValues(newPresenter);
            //Log.d(GYCMainActivity.TAG, "Updating presenter: " + presenter);
        } else {
            presenters.put(String.valueOf(id), newPresenter);
            //Log.d(GYCMainActivity.TAG, "Adding presenter: " + newPresenter);
        }
    }

    private void updatePresenterSlug(Integer id, String slug){
        if(presenters.containsKey(String.valueOf(id))){
            Presenter presenter = presenters.get(String.valueOf(id));
            presenter.setSlug(slug);
            //Log.d(GYCMainActivity.TAG, "Updating presenter slug: " + presenter + " to " + slug);
        } else {
            Log.d(GYCMainActivity.TAG, "No presenter with ID: " + id + ", cannot add slug");
        }
    }

    private void addSermonToPresenter(Integer id){
        if(presenters.containsKey(String.valueOf(id))){
            Presenter presenter = presenters.get(String.valueOf(id));
            presenter.setNumSermons(presenter.getNumSermons()+1);
            //Log.d(GYCMainActivity.TAG, "Updating presenter num sermons: " + presenter + " to " + presenter.getNumSermons());
        }
        else {
            Log.d(GYCMainActivity.TAG, "No presenter with ID: " + id + ", cannot add sermon");
        }
    }


    private void addEvent(Event newEvent){
        Integer id = newEvent.getId();
        if(events.containsKey(String.valueOf(id))){
            Event event = events.get(id);
            event.updateValues(newEvent);
            //Log.d(GYCMainActivity.TAG, "Updating event: " + event);
        } else {
            events.put(String.valueOf(id), newEvent);
            //Log.d(GYCMainActivity.TAG, "Adding event: " + newEvent);
        }
    }


    private void addSermon(Sermon sermon){
        Integer id = sermon.getId();
        sermons.put(String.valueOf(id), sermon);
        //Log.d(GYCMainActivity.TAG, "Adding sermon: " + sermon);
    }

    /**
     * Returns true if we have synced all the articles
      * @param activity
     * @return
     */
    public static boolean isFirstSyncComplete(Activity  activity){
        SharedPreferences prefs = activity.getSharedPreferences(activity.getString(R.string.GYC_PREFERENCES), Context.MODE_PRIVATE);
        return prefs.getBoolean(activity.getString(R.string.FIRST_SYNC_COMPLETE), false);
    }

    /**
     * Returns a hash map of all the presenters
     * @return
     */
    public HashMap<String, Presenter> getPresenters(){
        return presenters;
    }


    /**
     * Returns a hash map of events
     * @return
     */
    public HashMap<String, Event> getEvents(){
        return events;
    }

    /**
     * Crawls the page to get the audio file lin
     * @param uri
     * @return
     */
    public String getSermonAudioUrl(URL uri){
        HtmlCleaner cleaner = new HtmlCleaner();
        try {
            TagNode node = cleaner.clean(uri);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
