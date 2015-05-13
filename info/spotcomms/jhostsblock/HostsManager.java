package info.spotcomms.jhostsblock;

import java.io.*;
import java.nio.file.*;
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
    public File hostsHeaderFile = new File(configDir, "hostsHeader.txt");//The path of where the HOSTS header file is stored
    public ArrayList<String> hostsHeader;//Contains a header for the HOSTS file
    public File whitelistFile = new File(configDir, "whitelist.txt");//The path of where the whitelist file is stored
    public ArrayList<String> whitelist;//Contains user addresses we shouldn't block
    public File blacklistFile = new File(configDir, "blacklist.txt");//The path of where the blacklist file is stored
    public ArrayList<String> blacklist;//Contains user addresses we should block
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void generateFiles() {//Create needed folders/files
        try {
            configDir.mkdirs();
            hostsHeaderFile.createNewFile();
            whitelistFile.createNewFile();
            blacklistFile.createNewFile();
            blockListsFile.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {//Updates the HOSTS file with the latest lists
        try {
            for (String url : blockLists) {
                File out = new File(configDir + "/Downloaded_Lists", new String(MessageDigest.getInstance("MD5").digest(url.getBytes())) + utils.identifyFileType(url));//Generate the out path
                utils.downloadFile(url, out.toPath());//Download the file
                if(utils.identifyFileType(out.toString()).contains("txt")) {//Plaintext files
                    hostsFileOut.addAll(utils.readFileIntoArray(out));//Merge the new array with the out array
                } else {//Compressed files
                    hostsFileOut.addAll(utils.readCompressedFileIntoArray(out));//Merge the new array with the out array
                }
            }
            hostsFileOut.addAll(blacklist);//Merge the users blacklist array with the out array
            Collections.sort(hostsFileOut);//Sort the out array
            utils.removeDuplicates(hostsFileOut);//Remove any duplicates from the out array
            for(String line : whitelist) {//Remote whitelisted entries from the out array
                for(String host : hostsFileOut) {
                    if(host.contains(line)) {
                        hostsFileOut.remove(host);
                    }
                }
            }
            hostsFile.renameTo(hostsFileOld);
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hostsFile), "utf-8"));//Instantiate a Writer object pointed at the HOSTS file
            for(String line : hostsHeader) {//Write out the header to the HOSTS file
                writer.write(line);
            }
            for(String line : hostsFileOut) {//Write out the blacklisted hosts to the HOSTS file
                writer.write(line);
            }
        } catch(Exception e) {
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
            } else {
                hostsFile.delete();//Delete the current HOSTS file
                hostsFileOld.renameTo(hostsFile);//Rename the old HOSTS file to the actual one
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
