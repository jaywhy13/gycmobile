package jaywhy13.gycweb.screens;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.fragments.GYCDetailPageFragment;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.models.Model;
import jaywhy13.gycweb.models.Presenter;
import jaywhy13.gycweb.models.Sermon;

/**
 * Created by jay on 9/9/13.
 */
public class GYCPresenterDetail extends Activity {

    /**
     * The fragment that contains the entire page basically...
     */
    private GYCDetailPageFragment detailPageFragment;

    Presenter presenter = new Presenter();
    private SimpleCursorAdapter sca;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Grab the information from the intent and update the model
        updateModel();
        detailPageFragment = (GYCDetailPageFragment) getFragmentManager().findFragmentById(R.id.detailPageFragment);
        detailPageFragment.setPageTitle(getPageTitle());
        detailPageFragment.setPageSubTitle(getPageSubTitle());

        setupPageList();

    }

    /**
     * Returns the cursor adapter that will be used for the list view in the main fragment
     * @return
     */
    protected CursorAdapter getCursorAdapter(){
        if(sca == null){
            sca = new SimpleCursorAdapter(this, R.layout.menu_item, getCursor(), new String[]{Sermon.SERMON_TITLE},
                    new int [] {R.id.menu_caption}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }
        return sca;
    }

    /**
     * Returns the cursor for the main list on the page
     * @return
     */
    protected Cursor getCursor(){
        return new Presenter().getSermons(getContentResolver());
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
        //modelClicked(model);
    }



    /**
     * Returns a link to the detailPageFragment that contains the title, sub title, action area and list view
     * @return
     */
    public Fragment getDetailPageFragment() {
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


    /**
     * This is used to set the title of the page
     *
     * @return
     */
    public String getPageTitle() {
        return presenter.getName();
    }


    /**
     * This is used to set the sub title of the page
     *
     * @return
     */
    public String getPageSubTitle() {
        Resources resources = getResources();
        return resources.getString(R.string.presenter_list_page_title);
    }


}