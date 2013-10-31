package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.fragments.GYCListPageFragment;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.util.Verses;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterList extends Activity {

    TextView pageTitle;
    TextView pageSubTitle;
    GYCListPageFragment listFragment;
    SimpleCursorAdapter sca = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, getCursor(), new String[]{Presenter.PRESENTER_NAME},
                    new int [] {R.id.menu_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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

}