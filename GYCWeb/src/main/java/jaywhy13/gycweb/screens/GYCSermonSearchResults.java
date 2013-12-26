package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.fragments.GYCDetailPageFragment;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by Jay on 12/25/13.
 */
public class GYCSermonSearchResults extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter sca;
    TextView searchResults;
    String query;
    GYCDetailPageFragment detailPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchResults = (TextView) findViewById(R.id.search_results);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        detailPageFragment = (GYCDetailPageFragment) getFragmentManager().findFragmentById(R.id.detailPageFragment);
        detailPageFragment.hideHeadings();

        setupPageList();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent){
        Log.d(GYCMainActivity.TAG, "Handling the intent.. going to init the loader");
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getExtras().getString(SearchManager.QUERY);
            searchResults.setText("\"" + query + "\"");
            this.query = query;
            if(this.query != null && this.query.length() > 0){
                Log.d(GYCMainActivity.TAG, "Initializing loader...");
                getLoaderManager().initLoader(0, null, this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(GYCMainActivity.TAG, "Our loader is created");
        return new Sermon().getSearchResults(this, query);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(GYCMainActivity.TAG, "Swapping the cursor for the loader");
        sca.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(GYCMainActivity.TAG, "Resetting the loader");
        sca.swapCursor(null);
    }

    protected void setupPageList(){
        CursorAdapter adapter = getCursorAdapter();
        detailPageFragment.getPageListView().setAdapter(adapter);
        detailPageFragment.getPageListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //menuItemClicked(adapterView, i, view);
                    }
                }
        );
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


    /**
     * Returns a new instance of the model that is used for the list
     * @return
     */
    public Model getListModel(){
        return new Sermon();
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

}
