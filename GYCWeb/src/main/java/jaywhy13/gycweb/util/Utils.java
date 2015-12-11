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

    public static String getVerboseDuration(String duration){
        String pieces [] = duration.split(":");
        if(pieces.length == 2){ // e.g. 10:12
            // only say how many mins it was ...
            return Integer.parseInt(pieces[0]) + " mins";
        } else if(pieces.length == 3){
            int hours = Integer.parseInt(pieces[0]);
            int mins = Integer.parseInt(pieces[1]);
            return (hours > 0 ? hours + " hr " : "") + mins + " mins";
        }
        return "";
    }
}
