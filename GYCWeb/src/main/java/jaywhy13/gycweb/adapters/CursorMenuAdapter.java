package jaywhy13.gycweb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by jay on 9/25/13.
 */
public class CursorMenuAdapter extends SimpleCursorAdapter {

    public CursorMenuAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }



}
