package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.R;
import jaywhy13.gycweb.media.GYCMedia;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/9/13.
 */
public class GYCSermonDetail extends GYCPresenterDetail implements TextView.OnClickListener {

    Sermon sermon = new Sermon();
    TextView listenNow;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToMediaService();

        listenNow = (TextView) findViewById(R.id.listen_now_btn);
        downloadNow = (TextView) findViewById(R.id.download_btn);
        downloadNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sermon.isDownloaded() && !sermon.isDownloading()){
                    SermonDownloader.downloadSermon(GYCSermonDetail.this, sermon);
                    downloadNow.setBackgroundColor(getResources().getColor(R.color.downloading_bg));
                    downloadNow.setText(getResources().getString(R.string.downloading_txt));
                    downloadNow.setEnabled(false);
                }
            }
        });
    }

    /**
     * Updates the download button
     */
    public void updateDownloadButton(){

        // check if it's downloaded
        boolean downloaded = sermon.isDownloaded();
        boolean downloading = sermon.isDownloading();
        Log.d(GYCMainActivity.TAG, "Updating download button - downloaded: " + downloaded + ", downloading: " + downloading);
        if(!downloaded){
            if(downloading){
                downloadNow.setBackgroundColor(getResources().getColor(R.color.downloading_bg));
                downloadNow.setText(getResources().getString(R.string.downloading_txt));
                downloadNow.setEnabled(false);

            } else {
                // show the download option
                downloadNow.setBackgroundColor(getResources().getColor(R.color.download_bg));
                downloadNow.setText(getResources().getString(R.string.download_txt));
                downloadNow.setEnabled(true);
            }
        } else {
            // show the downloaded style
            downloadNow.setBackgroundColor(getResources().getColor(R.color.downloading_bg));
            downloadNow.setText(getResources().getString(R.string.downloaded_txt));
            downloadNow.setAlpha(0.7f);
            downloadNow.setEnabled(false);
        }

    }

    @Override
    protected void setupPageTitles() {
        getPageTitle().setText(sermon.getTitle());
        getPageSubTitle().setText(sermon.getPresenterName());
        if(!sermon.getVerboseDuration().isEmpty()){
            getPageDescription().setText(sermon.getVerboseDuration());
        } else {
            getPageDescription().setVisibility(View.GONE);
        }
        getDetailPageFragment().getListCaption().setText(getString(R.string.other_sermons_by) + " " + sermon.getPresenterName());
    }


    @Override
    protected CursorLoader getCursorLoader() {
        return sermon.getOtherSermonsByPresenter(this);
    }

    @Override
    protected CursorAdapter getCursorAdapter() {
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, null, new String[]{Sermon.SERMON_TITLE, Sermon.SERMON_DURATION},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption_right}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            sca.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int i) {
                    if(Sermon.SERMON_DURATION.equals(cursor.getColumnName(i))){
                        String duration = Utils.getVerboseDuration(cursor.getString(i));
                        ((TextView) view).setText(duration);
                        return true;
                    }
                    return false;
                }
            });
        }
        return sca;
    }


    @Override
    public Model getModel() {
        return sermon;
    }

    @Override
    public void setupActionArea() {
        // Setup the listen now button
        View view = getLayoutInflater().inflate(R.layout.listen_now, null);
        getDetailPageFragment().addAction(view);

        TextView listenNowLink = (TextView) view.findViewById(R.id.listen_now_btn);
        listenNowLink.setOnClickListener(this);
    }

    private void showLoading(){
        listenNow.setText(getString(R.string.listen_now_loading));
        listenNow.setBackgroundColor(getResources().getColor(R.color.listen_now_bg_loading));
    }

    private void restoreListenButton(){
        listenNow.setText(getString(R.string.listen_now));
        listenNow.setBackgroundColor(getResources().getColor(R.color.listen_now_bg));
    }

    private void updateListenButton(){
        if(isServiceBound()){
            if(getPlayer().isPlaying() || getPlayer().getSermon() != null){
                Sermon currentSermon = getPlayer().getSermon();
                if(sermon.getId() == getModel().getId()){
                    listenNow.setText(getString(R.string.listen_now_stop));
                    listenNow.setBackgroundColor(getResources().getColor(R.color.listen_now_bg_stop));
                } else {
                    restoreListenButton();
                }
            } else {
                restoreListenButton();
            }
        }
    }

    @Override
    protected void trackPlayed(Bundle extras) {
        super.trackPlayed(extras);
        updateListenButton();
    }

    @Override
    protected void trackStopped(Bundle extras) {
        super.trackStopped(extras);
        updateListenButton();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.listen_now_btn){
            TextView textView = (TextView) view;
            if(isServiceBound()){
                String text = textView.getText().toString();
                if(text.equals(getResources().getString(R.string.listen_now))){
                    getPlayer().play(sermon);
                    showLoading();
                } else if(text.equals(getString(R.string.listen_now_stop))){
                    getPlayer().stop();
                }
            }
        }
    }


}

