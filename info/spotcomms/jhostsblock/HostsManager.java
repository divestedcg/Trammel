package info.spotcomms.jhostsblock;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 5/13/15
 * Time; 11:37 AM
 */
public class HostsManager {

    Utils utils = new Utils();//Instantiate a new Utils class
    public File configDir = utils.getConfigDir();//The directory where we store configs
    public File hostsFile = utils.getHostsFile();//The path where the HOSTS file is stored
    public File hostsFileOld = new File(hostsFile + ".bak");//The path where the old HOSTS file is stored
    public File hostsHeaderFile = new File(configDir, "hostsheader.txt");//The path of where the HOSTS header file is stored
    public ArrayList<String> hostsHeader;//Contains a header for the HOSTS file
    public File whitelistFile = new File(configDir, "whitelist.txt");//The path of where the whitelist file is stored
    public ArrayList<String> whitelist;//Contains user addresses we shouldn't block
    public File blacklistFile = new File(configDir, "blacklist.txt");//The path of where the blacklist file is stored
    public ArrayList<String> blacklist;//Contains user addresses we should block
    public File blockListsDir = new File(configDir + "/Downloaded_Lists/");//The path of where blocklists are stored
    public File blockListsFile = new File(configDir, "blocklists.txt");//The path of where the blocklists file is stored
    public ArrayList<String> blockLists;//Contains user URLs with blocklists
    public ArrayList<String> hostsFileOut = new ArrayList<String>();//Contains the processed blacklisted hosts

    public HostsManager() {//Read the configs files in
        try {
            if(!configDir.exists()) {//Create needed folders/files if configDir doesn't exist
                generateFiles();
            }
            hostsHeader = utils.readFileIntoArray(hostsHeaderFile);
            whitelist = utils.readFileIntoArray(whitelistFile);
            blacklist = utils.readFileIntoArray(blacklistFile);
            blockLists = utils.readFileIntoArray(blockListsFile);
            System.out.println(whitelist.size() + " whitelisted entries loaded");
            System.out.println(blacklist.size() + " blacklisted entries loaded");
            System.out.println(blockLists.size() + " blocklists loaded");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void generateFiles() {//Create needed folders/files
        try {
            configDir.mkdirs();
            blockListsDir.mkdirs();
            hostsHeaderFile.createNewFile();
            whitelistFile.createNewFile();
            blacklistFile.createNewFile();
            blockListsFile.createNewFile();
            System.out.println("Created needed folders/files");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {//Updates the HOSTS file with the latest lists
        try {
            for (String url : blockLists) {
                File out = new File(blockListsDir, utils.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(url.getBytes("utf-8"))) + utils.identifyFileType(url));//Generate the out path
                utils.downloadFile(url, out.toPath());//Download the file
                if(utils.identifyFileType(out.toString()).contains("txt")) {//Plaintext files
                    hostsFileOut.addAll(utils.readFileIntoArray(out));//Merge the new array with the out array
                } else {//Compressed files
                    hostsFileOut.addAll(utils.readCompressedFileIntoArray(out));//Merge the new array with the out array
                }
            }
            System.out.println("Downloaded all lists");
            hostsFileOut.addAll(blacklist);//Merge the users blacklist array with the out array
            System.out.println("Merged user blacklist into out list");
            Collections.sort(hostsFileOut);//Sort the out array
            System.out.println("Sorted the out list");
            utils.removeDuplicates(hostsFileOut);//Remove any duplicates from the out array
            System.out.println("Removed duplicates from out list");
            int r = 0;
            for(String line : whitelist) {//Remote whitelisted entries from the out array
                for(String host : hostsFileOut) {
                    if(host.contains(line)) {
                        hostsFileOut.remove(host);
                        r++;
                    }
                }
            }
            System.out.println("Removed " + r + " entries that were whitelisted");
            hostsFile.renameTo(hostsFileOld);
            System.out.println("Backed up current HOSTS file");
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hostsFile), "utf-8"));//Instantiate a Writer object pointed at the HOSTS file
            int c = 0;
            for(String line : hostsHeader) {//Write out the header to the HOSTS file
                writer.write(line + "\n");
                c++;
            }
            for(String line : hostsFileOut) {//Write out the blacklisted hosts to the HOSTS file
                writer.write(line.replaceAll("\t", "") + "\n");
                c++;
            }
            System.out.println("Wrote out " + c + " entries to HOSTS file");
            System.out.println("Successfully updated HOSTS file");
        } catch(Exception e) {
            System.out.println("Failed to update HOSTS file");
            e.printStackTrace();
        }
    }

    public void rollback(boolean original) {//Rolls back the HOSTS file to the previous one or the original one
        try {
            if (original) {
                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hostsFile), "utf-8"));//Instantiate a Write object pointed at the HOSTS file
                for (String line : hostsHeader) {//Write out the header to the HOSTS file
                    writer.write(line);
                }
                System.out.println("Wrote out the original HOSTS file");
            } else {
                hostsFile.delete();//Delete the current HOSTS file
                System.out.println("Deleted current HOSTS file");
                hostsFileOld.renameTo(hostsFile);//Rename the old HOSTS file to the actual one
                System.out.println("Moved the old HOSTS file into place");
            }
            System.out.println("Successfully reverted HOSTS file");
        } catch(Exception e) {
            System.out.println("Failed to revert HOSTS file");
            e.printStackTrace();
        }
    }

}
