package jaywhy13.gycweb;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jay on 9/4/13.
 */
public class GYCListPageFragment extends Fragment {

    TextView pageTitle;
    TextView pageSubTitle;
    TextView pageSummary;
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

    /**
     * Sets the title for the page
     * @param title
     */
    public void setPageTitle(String title){
        if(pageTitle != null){
            pageTitle.setText(title);
        }
    }


    /**
     * Sets the sub title for the page
     * @param subTitle
     */
    public void setPageSubTitle(String subTitle){
        if(pageSubTitle != null){
            pageSubTitle.setText(subTitle);
        }
    }

    /**
     * Sets the summary for the page
     * @param summary
     */
    public void setPageSummary(String summary){
        if(pageSummary != null){
            pageSummary.setText(summary);
        }
    }

    /**
     * Adds a control to the action area on the page
     * @param view
     */
    public void addAction(View view){
        if(actionAreaView != null){
            actionAreaView.addView(view);
        }
    }



}