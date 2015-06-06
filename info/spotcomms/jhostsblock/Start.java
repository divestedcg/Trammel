package info.spotcomms.jhostsblock;

import java.util.prefs.Preferences;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 5/13/15
 * Time; 11:24 AM
 */
public class Start {

    public static void main(String[] args) {
        if (args.length > 0) {//Check if arguments were passed
            //Console mode
            Console c = new Console(isAdmin(), args);//Instantiate a new Console class
        } else {
            //Graphical mode
            GUI g = new GUI(isAdmin());//Instantiate a new GUI class
        }
    }

    //Credits: http://stackoverflow.com/a/23538961
    private static boolean isAdmin() {//Check if we've launched with administrative/root privileges
        try {
            Preferences prefs = Preferences.systemRoot();
            prefs.put("jhostsblock", "swag");//SecurityException on Windows
            prefs.remove("jhostsblock");
            prefs.flush();//BackingStoreException on Linux
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
