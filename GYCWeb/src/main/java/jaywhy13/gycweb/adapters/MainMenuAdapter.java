package jaywhy13.gycweb.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import jaywhy13.gycweb.R;

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

    private final Context context;

    public MainMenuAdapter(Context context, int textViewResourceId, String [] values, int [] icons){
        super(context, textViewResourceId, values);
        this.menuTitles = values;
        this.menuIcons = icons;
        this.context = context;
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

        ImageView icon = (ImageView) rowView.findViewById(R.id.menu_icon);
        TextView caption = (TextView) rowView.findViewById(R.id.menu_caption);

        icon.setImageResource(menuIconResource);
        caption.setText(menuTitle);

        return rowView;
    }
}
