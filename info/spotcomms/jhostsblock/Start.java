package info.spotcomms.jhostsblock;

import javax.swing.*;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 6/9/15
 * Time; 1:09 PM
 */
public class Start {

    public static void main(String[] args) {
        Utils utils = new Utils();
        if (!utils.getConfigDir().exists()) {
            new HostsManager().generateDefaults();
        }
        if (args.length > 0) {
            Console c = new Console(args);
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            GUI g = new GUI();
        }
    }

}
