package jaywhy13.gycweb.adapters;


import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;
import jaywhy13.gycweb.views.MainMenuLinearLayout;

/**
 * An adapter provided to manage all the different menus in the app. This adapter allows
 * the list of menu options to be passed in along side their icon resource listing.
 */
public class MainMenuAdapter extends ArrayAdapter<String> {

    /**
     * Stores the menu titles
     */
    private final String [] menuTitles;

    /**
     * Stores the resource links to the menu icons
     */
    private final int [] menuIcons;

    private int [] backgroundColors = null;

    private final Context context;

    public MainMenuAdapter(Context context, int textViewResourceId, String [] values, int [] icons){
        super(context, textViewResourceId, values);
        this.menuTitles = values;
        this.menuIcons = icons;
        this.context = context;
    }


    public MainMenuAdapter(Context context, int textViewResourceId, String [] values, int [] icons, int [] backgroundColors){
        super(context, textViewResourceId, values);
        this.menuTitles = values;
        this.menuIcons = icons;
        this.context = context;
        this.backgroundColors = backgroundColors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        if(convertView != null){
            rowView = convertView;
        } else {
            // inflate the menu item from resource
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.menu_item, parent, false);
        }

        // Setup the icons and the captions
        int menuIconResource = this.menuIcons[position];
        String menuTitle = this.menuTitles[position];

        MainMenuLinearLayout menuItem = (MainMenuLinearLayout) rowView.findViewById(R.id.menu_item_container);
        if(position % 2 == 0){
            menuItem.setDirection(MainMenuLinearLayout.DOWN);
        } else {
            menuItem.setDirection(MainMenuLinearLayout.UP);
        }

        if(backgroundColors != null){
            int backgroundColor = backgroundColors.length > position ? backgroundColors[position] : 0xFFFF0000;
            menuItem.setMenuBackgroundColor(backgroundColor);
        }

        ImageView icon = (ImageView) rowView.findViewById(R.id.menu_icon);
        TextView caption = (TextView) rowView.findViewById(R.id.menu_caption);

        icon.setImageResource(menuIconResource);
        caption.setText(menuTitle);



        return rowView;
    }
}
