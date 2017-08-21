/*
 * Copyright (c) 2015. Spot Communications
 */

package info.spotcomms.trammel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 6/9/15
 * Time; 7:08 PM
 */
public class GUI extends JFrame implements ActionListener {

    private final Utils utils = new Utils();
    private JPanel panContent;
    private JButton btnConfigureHeader;
    private JButton btnConfigureWhitelist;
    private JButton btnConfigureBlacklist;
    private JButton btnConfigureBlocklists;
    private JCheckBox chkCacheLists;
    private JButton btnClearCache;
    private JCheckBox chkOptimizeIPs;
    private JCheckBox chkOptimizeHosts;
    private JComboBox drpFormat;
    private JComboBox drpLocation;
    private JButton btnGenerate;
    private JButton btnRollback;
    private JButton btnReset;
    private JCheckBox chkOptimizeWWW;
    private boolean running = false;
    private boolean finished = false;

    public GUI() {
        setTitle("Trammel");
        setLocation(150, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panContent);
        pack();
        setVisible(true);
        btnConfigureHeader.addActionListener(this);
        btnConfigureWhitelist.addActionListener(this);
        btnConfigureBlacklist.addActionListener(this);
        btnConfigureBlocklists.addActionListener(this);
        chkCacheLists.addActionListener(this);
        btnClearCache.addActionListener(this);
        drpLocation.addActionListener(this);
        drpFormat.addActionListener(this);
        btnGenerate.addActionListener(this);
        btnRollback.addActionListener(this);
        btnReset.addActionListener(this);
        drpFormat.addItem("HOSTS");
        drpFormat.addItem("Domains Only");
        drpLocation.addItem(utils.getHostsFile() + "");
        drpLocation.addItem("Browse");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfigureHeader) {
            try {
                Desktop.getDesktop().edit(new File(utils.getConfigDir(), "header.conf"));
            } catch (Exception e1) {
                Editor txtEditor = new Editor("Header", new File(utils.getConfigDir(), "header.conf"));
            }
        }
        if (e.getSource() == btnConfigureWhitelist) {
            try {
                Desktop.getDesktop().edit(new File(utils.getConfigDir(), "whitelist.conf"));
            } catch (Exception e1) {
                Editor txtEditor = new Editor("Whitelist", new File(utils.getConfigDir(), "whitelist.conf"));
            }
        }
        if (e.getSource() == btnConfigureBlacklist) {
            try {
                Desktop.getDesktop().edit(new File(utils.getConfigDir(), "blacklist.conf"));
            } catch (Exception e1) {
                Editor txtEditor = new Editor("Blacklist", new File(utils.getConfigDir(), "blacklist.conf"));
            }
        }
        if (e.getSource() == btnConfigureBlocklists) {
            try {
                Desktop.getDesktop().edit(new File(utils.getConfigDir(), "blocklists.conf"));
            } catch (Exception e1) {
                Editor txtEditor = new Editor("Blocklists", new File(utils.getConfigDir(), "blocklists.conf"));
            }
        }
        if (e.getSource() == chkCacheLists) {
            btnClearCache.setEnabled(chkCacheLists.isSelected());
        }
        if (e.getSource() == btnClearCache) {
            File fleCacheDir = new File(utils.getConfigDir(), "cache");
            if (fleCacheDir.exists()) {
                utils.deleteDirectory(fleCacheDir.toPath());
                fleCacheDir.mkdir();
                utils.showAlert("Cache Manager", "Successfully cleared cache", JOptionPane.INFORMATION_MESSAGE);
            } else {
                utils.showAlert("Cache Manager", "Cache already empty", JOptionPane.WARNING_MESSAGE);
            }
        }
        if (e.getSource() == drpLocation) {
            if (drpLocation.getSelectedIndex() == 1) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    drpLocation.addItem(chooser.getSelectedFile());
                    drpLocation.setSelectedIndex(drpLocation.getItemCount() - 1);
                }
            }
        }
        if (e.getSource() == drpFormat) {
            switch (drpFormat.getSelectedIndex()) {
                case 0:
                    chkOptimizeHosts.setEnabled(true);
                    chkOptimizeIPs.setEnabled(true);
                    chkOptimizeWWW.setEnabled(false);
                    break;
                case 1:
                    chkOptimizeHosts.setEnabled(false);
                    chkOptimizeIPs.setEnabled(false);
                    chkOptimizeWWW.setEnabled(true);
                    utils.showAlert("Hosts Manager", "You MUST change the output file for this format or risk breaking network connectivity", JOptionPane.WARNING_MESSAGE);
                    break;
            }
        }
        if (e.getSource() == btnGenerate) {
            btnGenerate.setText("Running...");
            btnGenerate.setEnabled(false);
            btnRollback.setEnabled(false);
            btnReset.setEnabled(false);
            new HostsManager(chkCacheLists.isSelected(), chkOptimizeHosts.isSelected() && chkOptimizeHosts.isEnabled(), chkOptimizeIPs.isSelected() && chkOptimizeIPs.isEnabled(), chkOptimizeWWW.isSelected() && chkOptimizeWWW.isEnabled(), drpFormat.getSelectedIndex(), new File("" + drpLocation.getSelectedItem())).update();
            btnGenerate.setText("Generate");
            btnGenerate.setEnabled(true);
            btnRollback.setEnabled(true);
            btnReset.setEnabled(true);
        }
        if (e.getSource() == btnRollback) {
            btnRollback.setText("Running...");
            btnGenerate.setEnabled(false);
            btnRollback.setEnabled(false);
            btnReset.setEnabled(false);
            new HostsManager(chkCacheLists.isSelected(), chkOptimizeHosts.isSelected() && chkOptimizeHosts.isEnabled(), chkOptimizeIPs.isSelected() && chkOptimizeIPs.isEnabled(), chkOptimizeWWW.isSelected() && chkOptimizeWWW.isEnabled(), drpFormat.getSelectedIndex(), new File("" + drpLocation.getSelectedItem())).rollback();
            btnRollback.setText("Rollback");
            btnGenerate.setEnabled(true);
            btnRollback.setEnabled(true);
            btnReset.setEnabled(true);
        }
        if (e.getSource() == btnReset) {
            btnReset.setText("Running...");
            btnGenerate.setEnabled(false);
            btnRollback.setEnabled(false);
            btnReset.setEnabled(false);
            new HostsManager(chkCacheLists.isSelected(), chkOptimizeHosts.isSelected() && chkOptimizeHosts.isEnabled(), chkOptimizeIPs.isSelected() && chkOptimizeIPs.isEnabled(), chkOptimizeWWW.isSelected() && chkOptimizeWWW.isEnabled(), drpFormat.getSelectedIndex(), new File("" + drpLocation.getSelectedItem())).reset();
            btnReset.setText("Reset");
            btnGenerate.setEnabled(true);
            btnRollback.setEnabled(true);
            btnReset.setEnabled(true);
        }
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panContent = new JPanel();
        panContent.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "Configure Lists", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font(panel1.getFont().getName(), panel1.getFont().getStyle(), panel1.getFont().getSize())));
        btnConfigureHeader = new JButton();
        btnConfigureHeader.setText("Header");
        btnConfigureHeader.setToolTipText("Edit the header.conf file");
        panel1.add(btnConfigureHeader, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnConfigureWhitelist = new JButton();
        btnConfigureWhitelist.setText("Whitelist");
        btnConfigureWhitelist.setToolTipText("Edit the whitelist.conf file");
        panel1.add(btnConfigureWhitelist, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnConfigureBlacklist = new JButton();
        btnConfigureBlacklist.setText("Blacklist");
        btnConfigureBlacklist.setToolTipText("Edit the blacklist.conf file");
        panel1.add(btnConfigureBlacklist, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnConfigureBlocklists = new JButton();
        btnConfigureBlocklists.setText("Blocklists");
        btnConfigureBlocklists.setToolTipText("Edit the blocklists.conf file");
        panel1.add(btnConfigureBlocklists, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Configure Options", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font(panel2.getFont().getName(), panel2.getFont().getStyle(), panel2.getFont().getSize())));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("Caching"));
        chkCacheLists = new JCheckBox();
        chkCacheLists.setSelected(true);
        chkCacheLists.setText("Cache Lists");
        panel3.add(chkCacheLists, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnClearCache = new JButton();
        btnClearCache.setText("Clear Cache");
        btnClearCache.setToolTipText("Delete all the currently cached files");
        panel3.add(btnClearCache, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("Optimizations"));
        chkOptimizeIPs = new JCheckBox();
        chkOptimizeIPs.setSelected(true);
        chkOptimizeIPs.setText("Replace 0.0.0.0 with just 0");
        panel4.add(chkOptimizeIPs, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkOptimizeHosts = new JCheckBox();
        chkOptimizeHosts.setEnabled(true);
        chkOptimizeHosts.setSelected(false);
        chkOptimizeHosts.setText("Allow multiple hosts per line");
        panel4.add(chkOptimizeHosts, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkOptimizeWWW = new JCheckBox();
        chkOptimizeWWW.setText("Remove www.*");
        panel4.add(chkOptimizeWWW, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder("Output"));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder("Format"));
        drpFormat = new JComboBox();
        panel6.add(drpFormat, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder("Location"));
        drpLocation = new JComboBox();
        panel7.add(drpLocation, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panel8, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel8.setBorder(BorderFactory.createTitledBorder(null, "Run", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font(panel8.getFont().getName(), panel8.getFont().getStyle(), panel8.getFont().getSize())));
        btnGenerate = new JButton();
        btnGenerate.setText("Generate");
        btnGenerate.setToolTipText("Generates a file using the selected options and outputs it to the chosen location");
        panel8.add(btnGenerate, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRollback = new JButton();
        btnRollback.setText("Rollback");
        btnRollback.setToolTipText("Rolls back the selected file if possible");
        panel8.add(btnRollback, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnReset = new JButton();
        btnReset.setText("Reset");
        btnReset.setToolTipText("Resets the file to only contain the header");
        panel8.add(btnReset, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panContent;
    }
}
