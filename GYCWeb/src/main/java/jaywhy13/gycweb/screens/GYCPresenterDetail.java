package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import android.os.IBinder;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.fragments.GYCDetailPageFragment;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.media.GYCMedia;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterDetail extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private MediaReceiver mediaReceiver;

    private TextView pageTitle;
    private TextView pageSubTitle;
    private TextView pageDescription;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Grab the information from the intent and update the model
        updateModel();

        detailPageFragment = (GYCDetailPageFragment) getFragmentManager().findFragmentById(R.id.detailPageFragment);
        detailPageFragment.hideHeadings();

        pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageSubTitle = (TextView) findViewById(R.id.pageSubTitle);
        pageDescription = (TextView) findViewById(R.id.pageDescription);

        sidebar = (LinearLayout) findViewById(R.id.sidebar);


        setupPageTitles();
        setupPageList();
        setupActionArea();

        mediaReceiver = new MediaReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                onMediaPlayerIntent(intent);
            }
        };

        getLoaderManager().initLoader(1, null, this);

    }


    /**
     * Sets the title, sub title and hides the page description text view
     */
    protected void setupPageTitles(){
        int numSermons = presenter.getNumSermons();
        String subTitle = numSermons + " sermon" + (numSermons == 1 ? "" : "s");

        pageTitle.setText(presenter.getName());
        pageSubTitle.setText(subTitle);
        pageDescription.setVisibility(View.GONE);
        detailPageFragment.getListCaption().setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Add a broadcast listener..
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GYCMedia.ACTION_LOAD);
        intentFilter.addAction(GYCMedia.ACTION_PLAY);
        intentFilter.addAction(GYCMedia.ACTION_PAUSE);
        intentFilter.addAction(GYCMedia.ACTION_STOP);
        registerReceiver(mediaReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mediaReceiver);
    }

    private LinearLayout sidebar;

    /**
     * The fragment that contains the entire page basically...
     */
    private GYCDetailPageFragment detailPageFragment;

    Presenter presenter = new Presenter();
    protected SimpleCursorAdapter sca;

    private GYCMedia.GYCMediaPlayer mediaPlayer;
    private boolean serviceBound = false;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaPlayer = (GYCMedia.GYCMediaPlayer) iBinder;
            serviceBound = true;

            Log.d(GYCMainActivity.TAG, "Service connected... adding music sidebar...");

            // Check if we need to add the fragment
            GYCMedia.addMusicSidebar(mediaPlayer, GYCPresenterDetail.this, sidebar);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mediaPlayer = null;
            serviceBound = false;
        }
    };

    /**
     * This method is called to establish a connection to the GYCMedia service
     */
    public void connectToMediaService(){
        if(!serviceBound){
            Intent intent = new Intent(this, GYCMedia.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    public void disconnectFromMediaService(){
        unbindService(serviceConnection);
    }

    public boolean isServiceBound() {
        return serviceBound;
    }



    /**
     * Returns the cursor adapter that will be used for the list view in the main fragment
     * @return
     */
    protected CursorAdapter getCursorAdapter(){
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, null, new String[]{Sermon.SERMON_TITLE, Sermon.SERMON_PRESENTER_NAME},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }
        return sca;
    }

    /**
     * Adds content to the action area of the page
     */
    public void setupActionArea(){

    }

    /**
     * Returns the cursor for the main list on the page
     * @return
     */
    protected Loader<Cursor> getCursorLoader(){
        return presenter.getSermons(this);
    }

    /**
     * Sets the adapter for the list view in the main fragment and also
     * sets up this class as a listener, so that menuItemClicked is called
     * whenever we click an item in the menu.
     */
    protected void setupPageList(){
        CursorAdapter adapter = getCursorAdapter();
        detailPageFragment.getPageListView().setAdapter(adapter);
        detailPageFragment.getPageListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        menuItemClicked(adapterView, i, view);
                    }
                }
        );
    }


    protected Model getListModel(){
        return new Sermon();
    }

    /**
     * This method grabs the cursor, creates a model from it and calls modelClicked
     * @param adapterView
     * @param position
     * @param view
     */
    protected void menuItemClicked(AdapterView adapterView, int position, View view){
        Cursor cursor = sca.getCursor();
        Model model = getListModel();
        Model.cursorRowToModel(model, cursor);
        Log.d(GYCMainActivity.TAG, "You just clicked: " + model);
        modelClicked(model);
    }

    /**
     * This gives us the class that should be used for defining the Intent.
     * @return
     */
    public Class getClassForListItemIntent(){
        return GYCSermonDetail.class;
    }

    /**
     * This is called when a list item is clicked. menuItemClicked creates the model object and
     * calls this class.
     * @param model
     */
    public void modelClicked(Model model){
        Intent intent = new Intent(this, getClassForListItemIntent());
        intent.putExtra(BaseColumns._ID, model.getId());
        intent.putExtra("table_name", model.getTableName());
        intent.putExtra("model", model.getValues());
        startActivity(intent);
    }



    /**
     * Returns a link to the detailPageFragment that contains the title, sub title, action area and list view
     * @return
     */
    public GYCDetailPageFragment getDetailPageFragment() {
        return detailPageFragment;
    }

    /**
     * Return the model that will be updated from the Intent
     * @return
     */
    public Model getModel(){
        return presenter;
    }


    /**
     * This updates the model from the info in the Intent
     */
    public void updateModel(){
        Intent intent = getIntent();
        ContentValues values = intent.getExtras().getParcelable("model");
        getModel().updateValues(values);
    }


    public TextView getPageTitle() {
        return pageTitle;
    }


    /**
     * This is used to set the sub title of the page
     *
     * @return
     */
    public TextView getPageSubTitle() {
        return pageSubTitle;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(serviceConnection);
        } catch(IllegalArgumentException iae){
            // in case we didn't bind yet
        }
    }

    public GYCMedia.GYCMediaPlayer getPlayer(){
        return mediaPlayer;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        sca.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        sca.swapCursor(null);
    }

    public static class MediaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    protected void onMediaPlayerIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(intent.getAction().equals(GYCMedia.ACTION_LOAD)){
            trackLoading(extras);
        } else if(intent.getAction().equals(GYCMedia.ACTION_PLAY)){
            trackPlayed(extras);
        } else if(intent.getAction().equals(GYCMedia.ACTION_PAUSE)){
            trackPaused(extras);
        } else if(intent.getAction().equals(GYCMedia.ACTION_STOP)){
            trackStopped(extras);
        }
    }

    protected void trackLoading(Bundle extras){

    }

    protected void trackPlayed(Bundle extras){

    }

    protected void trackPaused(Bundle extras){

    }

    protected void trackStopped(Bundle extras){

    }


    public TextView getPageDescription() {
        return pageDescription;
    }
}