package jaywhy13.gycweb.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListView;

import jaywhy13.gycweb.R;

/**
 * Created by jay on 9/4/13.
 */
public class GYCDetailPageFragment extends Fragment {

    private ImageView pageIcon;
    private TextView pageTitle;
    private TextView pageSubTitle;
    private TextView pageSummary;
    private LinearLayout actionAreaView;


    private ListView pageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_page, container, false);

        // initialize the components and store them
        pageIcon = (ImageView) view.findViewById(R.id.pageIcon);
        pageTitle = (TextView) view.findViewById(R.id.pageTitle);
        pageSubTitle = (TextView) view.findViewById(R.id.pageSubTitle);
        pageSummary = (TextView) view.findViewById(R.id.pageSummary);
        actionAreaView = (LinearLayout) view.findViewById(R.id.actionAreaView);
        pageList = (ListView) view.findViewById(R.id.pageList);

        return view;
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

    public ListView getPageList() {
        return pageList;
    }

}