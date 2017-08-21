/*
 * Copyright (c) 2015. Spot Communications
 */

package info.spotcomms.trammel;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

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
        try {
            SevenZip.initSevenZipFromPlatformJAR();
        } catch (SevenZipNativeInitializationException e) {
            e.printStackTrace();
        }
        if (args.length > 0) {
            Console c = new Console(args);
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            GUI g = new GUI();
        }
    }

}
