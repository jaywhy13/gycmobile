package jaywhy13.gycweb.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.media.SermonDownloader;
import jaywhy13.gycweb.models.Sermon;
import jaywhy13.gycweb.screens.GYCSermonDetail;

/**
 * Created by Jay on 12/26/13.
 */
public class SermonDownloadTask extends AsyncTask {

    Context context;
    Sermon sermon;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public SermonDownloadTask(Context context, Sermon sermon){
        this.context = context;
        this.sermon = sermon;
        notificationBuilder = new NotificationCompat.Builder(context);
    }

    @Override
    protected void onPreExecute() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setContentTitle(sermon.getTitle());
        notificationBuilder.setContentText(sermon.getPresenterName());
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationManager.notify(sermon.getId(), notificationBuilder.build());
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            URL uri = new URL(sermon.getAudioUrl());
            String audioUrl = sermon.getAudioUrl();
            String [] pieces = audioUrl.split("/");
            String filename = SermonDownloader.getDownloadFolder() + "/" + pieces[pieces.length-1];

            URLConnection connection = uri.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();
            File sermonFile = new File(filename);
            if(sermonFile.exists()){
                if(sermonFile.length() == fileLength){
                    // this is already downloaded
                    SermonDownloader.sermonDownloaded(sermon, filename);
                    return null;
                } else {
                    // Re-download the file
                    sermonFile.delete();
                }
            }


            // Create the stream
            InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
            OutputStream os = new FileOutputStream(filename);
            int count = 0;
            byte [] data = new byte[1024];
            long total = 0;

            while((count = is.read(data)) != -1){
                total += count;
                os.write(data, 0, count);
                double progress = ((double) total / (double) fileLength) * 100;
                if(count % 500 == 0){
                    Log.d(GYCMainActivity.TAG, "Download progressed " + total + "/" + fileLength + " : " + progress);
                }
                notificationBuilder.setProgress(100, (int) progress, false);
                notificationManager.notify(sermon.getId(), notificationBuilder.build());
            }

            os.flush();

            // close the streams
            os.close();
            is.close();

            notificationBuilder.setContentText("Download complete");
            notificationBuilder.setProgress(0, 0, false);
            notificationManager.notify(sermon.getId(), notificationBuilder.build());

            // Let them know the sermon is done downloading
            SermonDownloader.sermonDownloaded(sermon, filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        GYCSermonDetail sermonDetail = (GYCSermonDetail) context;
        Log.d(GYCMainActivity.TAG, "Download is complete");
        sermonDetail.updateDownloadButton();
    }
}
