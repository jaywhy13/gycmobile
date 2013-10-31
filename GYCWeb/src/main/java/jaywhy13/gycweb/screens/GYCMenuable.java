package jaywhy13.gycweb.screens;

/**
 * Created by jay on 9/9/13.
 */
public interface GYCMenuable {

    /**
     * Returns a list of menu headings
     * @return
     */
    public String [] getMenuHeadings();

    /**
     * Returns a list of resource ids for the images for each menu item
     * @return
     */
    public int [] getMenuIcons();

    /**
     * Fired when someone clicks a menu item...
     * @param position
     * @param caption
     */
    public void menuItemClicked(int position, String caption);

    /**
     * Returns a list of colors for the triangle-rect background fill
     * @return
     */
    public int [] getMenuBackgroundColors();

}
