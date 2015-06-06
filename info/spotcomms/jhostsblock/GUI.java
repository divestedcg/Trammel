package info.spotcomms.jhostsblock;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 5/13/15
 * Time; 11:25 AM
 */
public class GUI {

    public GUI(boolean isAdmin) {
        if (!isAdmin) {
            //Display popup
            System.exit(0);
        } else {
            //GUI stuff here
        }
    }

}
