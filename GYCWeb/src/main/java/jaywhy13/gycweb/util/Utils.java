package jaywhy13.gycweb.util;

import android.content.res.Resources;

/**
 * Created by Jay on 12/25/13.
 */
public class Utils {

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
