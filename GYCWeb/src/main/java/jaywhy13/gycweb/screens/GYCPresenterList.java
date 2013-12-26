package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.fragments.GYCListPageFragment;
import jaywhy13.gycweb.media.GYCMedia;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.util.Verses;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterList extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected TextView pageTitle;
    protected TextView pageSubTitle;
    protected GYCListPageFragment listFragment;
    protected SimpleCursorAdapter sca = null;

    public void onCreate(Bundle savedInstanceState) {
        long now = System.currentTimeMillis();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getLoaderManager().initLoader(0, null, this);

        pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageSubTitle = (TextView) findViewById(R.id.pageSubTitle);
        listFragment = (GYCListPageFragment) getFragmentManager().findFragmentById(R.id.mainPageFragment);

        setupPage();
        setupPageList();
    }


    /**
     * Sets up the page, hides different controls and so on...
     */
    protected void setupPage(){
        // Setup our page
        pageTitle.setText(getPageTitle());
        pageSubTitle.setText(Verses.getVerse());
        listFragment.hideHeadings();
    }


    /**
     * Returns the cursor adapter that will be used for the list view in the main fragment
     * @return
     */
    protected CursorAdapter getCursorAdapter(){
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, null, new String[]{Presenter.PRESENTER_NAME, Presenter.PRESENTER_NUM_SERMONS},
                    new int [] {R.id.menu_caption, R.id.menu_sub_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            sca.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int i) {
                    if(cursor.getColumnName(i).equals(Presenter.PRESENTER_NUM_SERMONS)){
                        TextView textView = (TextView) view;
                        int numSermons = cursor.getInt(i);
                        String caption = numSermons == 1 ? "1 sermon" : numSermons + " sermons";
                        textView.setText(caption);
                        return true;
                    }
                    return false;
                }
            });
        }
        return sca;
    }

    /**
     * Returns the cursor for the main list on the page
     * @return
     */
    protected Cursor getCursor(){
        return new Presenter().get(getContentResolver());
    }

    /**
     * Sets the adapter for the list view in the main fragment and also
     * sets up this class as a listener, so that menuItemClicked is called
     * whenever we click an item in the menu.
     */
    protected void setupPageList(){
        CursorAdapter adapter = getCursorAdapter();
        listFragment.getPageListView().setAdapter(adapter);
        listFragment.getPageListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        menuItemClicked(adapterView, i, view);
                    }
                }
        );
    }

    /**
     * Returns a new instance of the model that is used for the list
     * @return
     */
    public Model getListModel(){
        return new Presenter();
    }

    /**
     * This gives us the class that should be used for defining the Intent.
     * @return
     */
    public Class getClassForListItemIntent(){
        return GYCPresenterDetail.class;
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
     * Returns the page title
     * @return
     */
    public String getPageTitle(){
        return getString(R.string.presenter_list_page_title);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new Presenter().getViaCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        sca.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        sca.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        // We need to associate the searchable configuration with the SearchView (attached to the search menu item)
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Change the color of the search text
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }
}