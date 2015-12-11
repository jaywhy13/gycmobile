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
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    protected MediaReceiver mediaReceiver;

    protected boolean receiverRegistered = false;

    private TextView pageTitle;
    private TextView pageSubTitle;
    private TextView pageDescription;
    private TextView listCaption;
    protected ListView pageListView;
    protected LinearLayout actionArea;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Grab the information from the intent and update the model
        updateModel();

        pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageSubTitle = (TextView) findViewById(R.id.pageSubTitle);
        pageDescription = (TextView) findViewById(R.id.pageDescription);
        pageListView = (ListView) findViewById(R.id.pageList);

        sidebar = (LinearLayout) findViewById(R.id.sidebar);

        listCaption = (TextView) findViewById(R.id.listCaption);
        actionArea = (LinearLayout) findViewById(R.id.actionAreaView);

        setupPageTitles();
        setupPageList();
        setupActionArea();

        getLoaderManager().initLoader(1, null, this);

        connectToMediaService();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.activity_open_slide, R.anim.activity_close_shrink);
    }

    /**
     * Adds a control to the action area on the page
     *
     * @param view
     */
    public void addAction(View view) {
        if (actionArea != null) {
            actionArea.addView(view);
        }
    }




    public TextView getListCaption() {
        return listCaption;
    }



    public ListView getPageListView() {
        return pageListView;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // closing transitions
        overridePendingTransition(R.anim.activity_open_grow, R.anim.activity_close_slide);
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
        }


    @Override
    protected void onResume(){
        super.onResume();
        if(mediaReceiver != null && !receiverRegistered){
            registerReceiver(mediaReceiver, mediaReceiver.getIntentFilter());
            receiverRegistered = true;
        }

        if(mediaReceiver != null){
            mediaReceiver.refresh();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaReceiver != null && receiverRegistered){
            unregisterReceiver(mediaReceiver);
            receiverRegistered = false;
        }
    }

    protected LinearLayout sidebar;

    Presenter presenter = new Presenter();
    protected SimpleCursorAdapter sca;

    protected GYCMedia.GYCMediaPlayer mediaPlayer;
    private boolean serviceBound = false;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaPlayer = (GYCMedia.GYCMediaPlayer) iBinder;
            serviceBound = true;
            mediaReceiver = createMediaPlayer();
            if(!receiverRegistered){
                Log.d(GYCMainActivity.TAG, "Receiver registered");
                registerReceiver(mediaReceiver, mediaReceiver.getIntentFilter());
                receiverRegistered = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mediaPlayer = null;
            serviceBound = false;
        }
    };

    public MediaReceiver createMediaPlayer(){
        return new MediaReceiver(this, sidebar, mediaPlayer);
    }

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
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View menuParent = super.getView(position, convertView, parent);
                    final ViewGroup menuItem = (ViewGroup) menuParent.findViewById(R.id.menu_item_container);
                    //menuItem.setBackgroundResource(R.drawable.menu_item_bg);
                    return menuParent;
                }
            };
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
        getPageListView().setAdapter(adapter);
        getPageListView().setOnItemClickListener(
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
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

        public Context context;
        public ViewGroup sidebarContainer;
        public GYCMedia.GYCMediaPlayer mediaPlayer;
        IntentFilter intentFilter = new IntentFilter();


        public MediaReceiver(Context context, ViewGroup sidebarContainer, GYCMedia.GYCMediaPlayer mediaPlayer){
            super();
            this.context = context;
            this.sidebarContainer = sidebarContainer;
            this.mediaPlayer = mediaPlayer;

            // Add the music sidebar
            sidebarContainer.setVisibility(View.GONE);
            GYCMedia.addMusicSidebar(mediaPlayer, context, sidebarContainer);
            refresh();

            // Add the intent filters...
            intentFilter.addAction(GYCMedia.ACTION_LOAD);
            intentFilter.addAction(GYCMedia.ACTION_PLAY);
            intentFilter.addAction(GYCMedia.ACTION_PAUSE);
            intentFilter.addAction(GYCMedia.ACTION_STOP);

        }

        public void hideSidebar(){
            Log.d(GYCMainActivity.TAG, "Hiding the sidebar");
            sidebarContainer.setVisibility(View.GONE);
        }

        public void showSidebar(){
            Log.d(GYCMainActivity.TAG, "Showing the sidebar");
            sidebarContainer.setVisibility(View.VISIBLE);
        }

        /**
         * Checks the media player status to see if the side bar should be showing or not
         */
        public void refresh(){
            if(mediaPlayer.isPlaying() || mediaPlayer.isPaused()){
                showSidebar();
            } else {
                hideSidebar();
            }
            Sermon sermon = mediaPlayer.getSermon();
            if(sermon != null){

            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(GYCMainActivity.TAG, "Received media player action: " + intent.getAction());
            refresh();
        }

        public IntentFilter getIntentFilter(){
            return intentFilter;
        }

    }


    public TextView getPageDescription() {
        return pageDescription;
    }
}