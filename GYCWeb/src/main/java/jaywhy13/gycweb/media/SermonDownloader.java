package jaywhy13.gycweb.media;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.models.Sermon;
import jaywhy13.gycweb.tasks.SermonDownloadTask;

/**
 * Created by Jay on 12/26/13.
 */
public class SermonDownloader {

    private static HashMap<String, SermonDownloadTask> tasks = new HashMap<String, SermonDownloadTask>();
    private static Context context;
    private static HashMap<String, String> completedTasks = new HashMap<String, String>();


    public static String getDownloadFolder(){
        String downloadFolder = Environment.getExternalStorageDirectory() + "/CIA/sermons";
        File folder = new File(downloadFolder);
        if(!folder.exists()){
            folder.mkdirs();
        }
        return downloadFolder;
    }


    /**
     * Starts a download task to download sermon
     * @param sermon
     */
    public static void downloadSermon(Context context, Sermon sermon){
        SermonDownloader.context = context;
        SermonDownloadTask task = new SermonDownloadTask(context, sermon);
        String key = getSermonKey(sermon);
        tasks.put(key, task);
        Log.d(GYCMainActivity.TAG, "Downloading " + sermon.getTitle() + ", there are " + tasks.size() + " sermons downloading now");
        task.execute();
    }

    /**
     * This is called by the Async Task is downloaded
     * @param sermon
     */
    public static void sermonDownloaded(Sermon sermon, String filename){
        if(isSermonDownloading(sermon)){
            String key = getSermonKey(sermon);
            tasks.remove(key);
            completedTasks.put(key, key);
            ContentValues values = new ContentValues();
            values.put(Sermon.SERMON_FILE_URL, filename);
            sermon.update(context.getContentResolver(), values);
            sermon.setSermonFileUrl(filename);
        }
    }

    /**
     * Returns True if the sermon is currently downloading
     * @param sermon
     * @return
     */
    public static boolean isSermonDownloading(Sermon sermon){
        String key = getSermonKey(sermon);
        boolean result = tasks.containsKey(key);
        Log.d(GYCMainActivity.TAG, "There are " + tasks.size() + " sermons downloading");
        return result;
    }

    /**
     * Returns true if this sermon was downloaded while the app was open.. yes
     * @return
     */
    public static boolean isSermonDownloaded(Sermon sermon){
        return completedTasks.containsKey(getSermonKey(sermon));
    }

    public static String getSermonKey(Sermon sermon){
        return "sermon-" + sermon.getId();
    }

}
