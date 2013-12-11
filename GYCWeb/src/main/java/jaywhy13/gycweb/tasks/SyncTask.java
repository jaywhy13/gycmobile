package jaywhy13.gycweb.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.PreferencesFactory;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Event;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;
import jaywhy13.gycweb.net.Internet;

/**
 * This class parses the data.json file and populates the database.
 * Created by jay on 9/12/13.
 */
public class SyncTask extends AsyncTask {

    private final Context context;

    public SyncTask(Context context){
        this.context = context;
    }

    private HashMap<String, Presenter> presenters = new HashMap<String, Presenter>();
    private HashMap<String, Sermon> sermons = new HashMap<String, Sermon>();
    private HashMap<String, Event> events = new HashMap<String, Event>();

    @Override
    protected Object doInBackground(Object[] objects) {
        // Load the data from resources
        InputStream is = context.getResources().openRawResource(R.raw.data);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = br.readLine()) != null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonStr = sb.toString();
        int hashCode = jsonStr.hashCode() + Model.DATABASE_NAME.hashCode();

        // Check if this has changed...
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int _hashCode = sharedPreferences.getInt(context.getString(R.string.json_hash), 0);
        if(hashCode == _hashCode){
            return "Done";
        }

        try {
            JSONObject obj = new JSONObject(jsonStr);
            // Add events, presenters and sermons.. in that order
            JSONArray eventsJson = obj.getJSONArray("events");
            for(int e = 0; e < eventsJson.length(); e++){
                JSONObject eventJson = eventsJson.getJSONObject(e);
                int eventId = Integer.valueOf(eventJson.getString("id"));
                String name = eventJson.getString("name");
                addEvent(new Event(Integer.valueOf(eventId), name));
            }

            JSONArray presentersJson = obj.getJSONArray("presenters");
            for(int p = 0; p < presentersJson.length(); p++){
                JSONObject presenterJson = presentersJson.getJSONObject(p);
                int presenterId = Integer.valueOf(presenterJson.getString("id"));
                String name = presenterJson.getString("name");
                String siteUrl = presenterJson.getString("site_url");
                int numSermons = presenterJson.getInt("num_sermons");
                // Add the presenter
                Presenter presenter = new Presenter(presenterId, name);
                presenter.setSiteUrl(siteUrl);
                presenter.setNumSermons(numSermons);
                addPresenter(presenter);
            }


            JSONArray sermonsJson = obj.getJSONArray("sermons");
            for(int s = 0; s < sermonsJson.length(); s++){
                JSONObject sermonJson = sermonsJson.getJSONObject(s);
                int sermonId = sermonJson.getInt("id");
                String title = sermonJson.getString("title");
                String siteUrl = sermonJson.getString("site_url");
                String audioUrl = sermonJson.getString("audio_url");
                long date = sermonJson.getLong("date");
                String duration = sermonJson.getString("duration");

                // Add the sermon
                Sermon sermon = new Sermon(sermonId, title);
                sermon.setSiteUrl(siteUrl);
                sermon.setAudioUrl(audioUrl);
                sermon.setDate(String.valueOf(date));
                sermon.setDuration(duration);

                // Set the presenter info...
                int presenterId = sermonJson.getInt("presenter_id");
                String presenterName = sermonJson.getString("presenter_name");
                sermon.setPresenterId(presenterId);
                sermon.setPresenterName(presenterName);

                // Set the event info...
                try {
                int eventId = sermonJson.getInt("event_id");
                sermon.setEventId(eventId);
                } catch(JSONException je){
                    // no big deal...
                }

                addSermon(sermon);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            //Log.d(GYCMainActivity.TAG, "Adding presenter to database: " + presenter);
            presenter.insert(context.getContentResolver());
        }

        new Sermon().deleteAll(context.getContentResolver());
        // Add the presenters to the database
        Iterator<String> sermonIdsIterator = sermons.keySet().iterator();
        while(sermonIdsIterator.hasNext()){
            String id = sermonIdsIterator.next();
            Sermon sermon = sermons.get(id);
            //Log.d(GYCMainActivity.TAG, "Adding presenter to database: " + presenter);
            sermon.insert(context.getContentResolver());
        }

        // Update preferences...
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.json_hash), hashCode);
        editor.commit();

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



}
