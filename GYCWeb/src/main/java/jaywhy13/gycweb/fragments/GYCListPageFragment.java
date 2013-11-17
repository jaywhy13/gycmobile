package jaywhy13.gycweb.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.screens.GYCMenuable;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.adapters.MainMenuAdapter;

/**
 * This class encapsulates the logic of a list page fragment in the app. It abstracts away the
 * menu by providing easy methods for populating and responding to menu events.
 * Created by jay on 9/4/13.
 */
public class GYCListPageFragment extends Fragment {

    private int menuItemResource = R.layout.menu_item;
    /**
     * The title for the page
     */
    TextView pageTitle;

    /**
     * The sub title for the page
     */
    TextView pageSubTitle;

    /**
     * A detail section
     */
    TextView pageSummary;


    /**
     * The list view component
     */
    ListView pageListView;

    LinearLayout actionAreaView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_page, container, false);
        pageTitle = (TextView) view.findViewById(R.id.pageTitle);
        pageSubTitle = (TextView) view.findViewById(R.id.pageSubTitle);
        pageSummary = (TextView) view.findViewById(R.id.pageSummary);
        pageListView = (ListView) view.findViewById(R.id.pageList);
        actionAreaView = (LinearLayout) view.findViewById(R.id.actionAreaView);
        return view;
    }

    public int getMenuItemResource(){
        return menuItemResource;
    }

    public void setMenuItemResource(int menuItemResource) {
        this.menuItemResource = menuItemResource;
    }

    /**
     * This method checks if the Activity implements GYCMenuable and
     * adds the menu headings and icons as is needed to populate the menu.
     */
    public void setupPageList() {
        if (getActivity() instanceof GYCMenuable) {
            final GYCMenuable menuable = (GYCMenuable) getActivity();
            final String menuHeadings[] = menuable.getMenuHeadings();
            final int menuIcons[] = menuable.getMenuIcons();
            final int backgroundColors [] = menuable.getMenuBackgroundColors();
            MainMenuAdapter adapter = new MainMenuAdapter(getActivity(), getMenuItemResource(), menuHeadings, menuIcons, backgroundColors);
            pageListView.setAdapter(adapter);
            ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String menuCaption = menuHeadings[i];
                    Log.d(GYCMainActivity.TAG, "The user clicked: " + menuCaption);
                    menuable.menuItemClicked(i, menuCaption);
                }
            };
            pageListView.setOnItemClickListener(listener);
        }
    }


    /**
     * Sets the title for the page
     *
     * @param title
     */
    public void setPageTitle(String title) {
        if (pageTitle != null) {
            pageTitle.setText(title);
        }
    }


    /**
     * Sets the sub title for the page
     *
     * @param subTitle
     */
    public void setPageSubTitle(String subTitle) {
        if (pageSubTitle != null) {
            pageSubTitle.setText(subTitle);
        }
    }

    /**
     * Sets the summary for the page
     *
     * @param summary
     */
    public void setPageSummary(String summary) {
        if (pageSummary != null) {
            pageSummary.setText(summary);
        }
    }

    /**
     * Adds a control to the action area on the page
     *
     * @param view
     */
    public void addAction(View view) {
        if (actionAreaView != null) {
            actionAreaView.addView(view);
        }
    }

    public ListView getPageListView() {
        return pageListView;
    }

    public void hidePageTitle(){
        if(pageTitle != null){
            pageTitle.setVisibility(View.GONE);
        }
    }

    public void hidePageSubTitle(){
        if(pageTitle != null){
            pageSubTitle.setVisibility(View.GONE);
        }
    }

    public void hidePageSummary(){
        if(pageTitle != null){
            pageSummary.setVisibility(View.GONE);
        }
    }

    /**
     * Hides the title, sub title and summary on the page
     */
    public void hideHeadings() {
        hidePageTitle();
        hidePageSubTitle();
        hidePageSubTitle();
        hidePageSummary();
    }
}