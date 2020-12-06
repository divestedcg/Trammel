/*
Copyright (c) 2015-2017 Divested Computing Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package info.spotcomms.trammel;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 6/9/15
 * Time; 7:56 PM
 */
class HostsManager {

    private final Utils utils = new Utils();
    private final File dirConfigs = utils.getConfigDir();
    private final File dirCache = new File(dirConfigs, "cache");
    private final File fleHeader = new File(dirConfigs, "header.conf");
    private ArrayList<String> arrHeader;
    private final File fleWhitelist = new File(dirConfigs, "whitelist.conf");
    private ArrayList<String> arrWhitelist;
    private final File fleBlacklist = new File(dirConfigs, "blacklist.conf");
    private ArrayList<String> arrBlacklist;
    private final File fleBlocklists = new File(dirConfigs, "blocklists.conf");
    private ArrayList<String> arrBlocklists;
    private final Set<String> arrDomains = new HashSet<>();
    private final Set<String> arrOut = new HashSet<>();
    private boolean cache;
    private boolean optimizeHosts;
    private boolean optimizeIPs;
    private boolean optimizeWWW;
    private int format;
    private File fleOutput;
    private File fleOutputOld;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public HostsManager() {

    }

    public HostsManager(boolean cache, boolean optimizeHosts, boolean optimizeIPs, boolean optimizeWWW, int format, File fleOutput) {
        this.cache = cache;
        this.optimizeHosts = optimizeHosts;
        this.optimizeIPs = optimizeIPs;
        this.optimizeWWW = optimizeWWW;
        this.format = format;
        this.fleOutput = fleOutput;
        fleOutputOld = new File(fleOutput + ".bak");
        arrHeader = utils.readFileIntoArray(fleHeader);
        arrWhitelist = utils.readHostsFileIntoArray(fleWhitelist);
        arrBlacklist = utils.readHostsFileIntoArray(fleBlacklist);
        arrBlocklists = utils.readFileIntoArray(fleBlocklists);
        arrBlocklists.sort(String::compareTo);
    }

    public void generateDefaults() {
        try {
            dirConfigs.mkdirs();
            dirCache.mkdirs();
            fleHeader.createNewFile();
            fleWhitelist.createNewFile();
            fleBlacklist.createNewFile();
            fleBlocklists.createNewFile();
            //TODO Possibly set some defaults
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            for (String url : arrBlocklists) {
                url = url.split(",")[0];
                File out = new File(dirCache, Utils.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(url.getBytes("utf-8"))) + utils.identifyFileType(url));
                utils.downloadFile(url, out.toPath(), cache);
                int preAddCount = arrDomains.size();
                ArrayList<String> toAdd = utils.readHostsFileIntoArray(out);
                arrDomains.addAll(toAdd);
                System.out.println("Added " + (arrDomains.size() - preAddCount) + "/" + toAdd.size() + " domains from " + url);
            }
            arrDomains.addAll(arrBlacklist);
            int c = 0;
            for (String domain : arrWhitelist) {
                arrDomains.remove(domain);
                c++;
            }
            System.out.println("Removed " + c + " whitelisted domains");
            ArrayList<String> arrDomainsNew = new ArrayList<>();
            arrDomainsNew.addAll(arrDomains);
            Collections.sort(arrDomainsNew);
            switch (format) {
                case 0:
                    if (optimizeHosts) {
                        StringBuilder line = new StringBuilder();
                        for (int x = 0; x < arrDomainsNew.size(); x++) {
                            String domain = arrDomainsNew.get(x);
                            if (x == (arrDomainsNew.size() - 1)) {
                                line.append(domain);
                                if (optimizeIPs)
                                    arrOut.add("0 " + line);
                                else
                                    arrOut.add("0.0.0.0 " + line);
                            } else if (line.toString().split(" ").length >= 5) {
                                if (optimizeIPs)
                                    arrOut.add("0 " + line);
                                else
                                    arrOut.add("0.0.0.0 " + line);
                                line = new StringBuilder(domain + " ");
                            } else {
                                line.append(domain).append(" ");
                            }
                        }
                    } else {
                        if (optimizeIPs)
                            for (String domain : arrDomainsNew)
                                arrOut.add("0 " + domain);
                        else
                            for (String domain : arrDomainsNew)
                                arrOut.add("0.0.0.0 " + domain);
                    }
                    break;
                case 1:
                    if (optimizeWWW) {
                        for (String domain : arrDomainsNew) {
                            if (domain.startsWith("www.") && domain.split("\\.").length == 3) {
                                arrOut.add(domain.split("www.")[0]);
                            } else {
                                arrOut.add(domain);
                            }
                        }
                    } else
                        arrOut.addAll(arrDomainsNew);
                    break;
            }
            ArrayList<String> arrOutNew = new ArrayList<>();
            arrOutNew.addAll(arrOut);
            Collections.sort(arrOutNew);
            fleOutput.renameTo(fleOutputOld);
            PrintWriter writer = new PrintWriter(fleOutput, "UTF-8");
            if (format == 0) {
                for (String line : arrHeader) {
                    writer.println(line);
                }
            }
            writer.println("#\n#Created using Trammel");
            writer.println("#Last Updated: " + dateFormat.format(Calendar.getInstance().getTime()));
            writer.println("#Number of Entries: " + arrOutNew.size());
            writer.println("#\n#Created from the following lists");
            writer.println("#All attempts have been made to ensure accuracy of the corresponding license files.");
            writer.println("#If you would like your list removed from this list please email us at webmaster@[THIS DOMAIN]");
            for (String list : arrBlocklists) {
                String[] listS = list.split(",");
                writer.println("#License: " + listS[1] + " - " + listS[0]);
            }
            writer.println("#\n");
            for (String line : arrOutNew) {
                writer.println(line);
            }
            writer.close();
            utils.showAlert("Trammel", "Successfully exported domains to file", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            utils.showAlert("Trammel", "Failed to generate file", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void rollback() {
        try {
            fleOutput.delete();
            fleOutputOld.renameTo(fleOutput);
            utils.showAlert("Trammel", "Successfully rolled back file", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            utils.showAlert("Trammel", "Failed to rollback file", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reset() {
        try {
            fleOutput.delete();
            PrintWriter writer = new PrintWriter(fleOutput, "UTF-8");
            for (String line : arrHeader) {
                writer.println(line);
            }
            writer.close();
            utils.showAlert("Trammel", "Successfully reset file", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            utils.showAlert("Trammel", "Failed to reset file", JOptionPane.ERROR_MESSAGE);
        }
    }

}
