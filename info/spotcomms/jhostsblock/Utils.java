package info.spotcomms.jhostsblock;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 6/9/15
 * Time; 1:11 PM
 */
public class Utils {

    String ipAddressRegex = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";//Credit: http://stackoverflow.com/a/5667402
    String hostnameRegex = "(?:(?:(?:(?:[a-zA-Z0-9][-a-zA-Z0-9]{0,61})?[a-zA-Z0-9])[.])*(?:[a-zA-Z][-a-zA-Z0-9]{0,61}[a-zA-Z0-9]|[a-zA-Z])[.]?)";//Credit: http://stackoverflow.com/a/1418724

    //Credit: http://stackoverflow.com/a/4895572
    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++)
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        return result;
    }

    //Credit: http://fahdshariff.blogspot.ru/2011/08/java-7-deleting-directory-by-walking.html
    public void deleteDirectory(Path dir) {
        try {
            System.gc();
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String url, Path out, boolean useCache) {
        try {
            if(useCache && out.toFile().exists()) {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 Gecko Firefox");
                connection.setIfModifiedSince(out.toFile().lastModified());
                connection.connect();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_NOT_MODIFIED)
                    Files.copy(new URL(url).openStream(), out, StandardCopyOption.REPLACE_EXISTING);
                connection.disconnect();
            } else {
                Files.copy(new URL(url).openStream(), out, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getConfigDir() {
        File configDir = new File("");
        switch (getOS()) {
            case "Linux":
                configDir = new File("/etc/jhostsblock/");
                break;
            case "Mac":
                configDir = new File(
                    System.getProperty("user.home") + "/Library/Application Support/JHostsBlock/");
                break;
            case "Windows":
                configDir = new File(System.getenv("AppData") + "/JHostsBlock/");
                break;
        }
        return configDir;
    }

    public File getHostsFile() {
        File hostsFile = new File("");
        switch (getOS()) {
            case "Linux":
                hostsFile = new File("/etc/", "hosts");
                break;
            case "Mac":
                hostsFile = new File("/private/etc/", "hosts");
                break;
            case "Windows":
                hostsFile = new File("C:/System32/drivers/etc/", "hosts");
                break;
        }
        return hostsFile;
    }

    public String getOS() {
        try {
            String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (os.contains("linux"))
                return "Linux";
            if (os.startsWith("mac"))
                return "Mac";
            if (os.startsWith("win"))
                return "Windows";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public String identifyFileType(String url) {
        String extension = ".txt";
        if (url.contains("zip"))
            extension = ".zip";
        else if (url.contains("gz"))
            extension = ".gz";
        return extension;
    }

    //Credit: http://stackoverflow.com/a/23538961
    private static boolean isAdmin() {
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

    public ArrayList<String> readFileIntoArray(File in) {
        ArrayList<String> out = new ArrayList<String>();
        try {
            Scanner fileIn = new Scanner(in);
            while (fileIn.hasNext()) {
                String line = fileIn.nextLine();
                if (!line.contains("#"))
                    out.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public ArrayList<String> readHostsFileIntoArray(File in) {
        ArrayList<String> out = new ArrayList<String>();
        try {
            Scanner fileIn = null;
            if (identifyFileType(in.toString()).contains(".txt")) {//Plain text
                fileIn = new Scanner(in);
            }
            if (identifyFileType(in.toString()).contains(".zip")) {//Decompress ZIP
                //Credit: http://stackoverflow.com/a/14656534 and http://stackoverflow.com/a/18974782
                ZipFile compressedList = new ZipFile(in);
                ArrayList compressedFiles = (ArrayList) compressedList.getFileHeaders();
                for (Object file : compressedFiles) {
                    FileHeader newFile = (FileHeader) file;
                    if (newFile.getFileName().equalsIgnoreCase("hosts") || newFile.getFileName().startsWith("hosts")) {
                        fileIn = new Scanner(compressedList.getInputStream(newFile));
                    }
                }
            }
            if (identifyFileType(in.toString()).contains(".gz")) {//Decompress GunZip
                fileIn = new Scanner(new GZIPInputStream(new FileInputStream(in)));
            }
            while (fileIn.hasNext()) {
                String line = fileIn.nextLine();
                if(!line.contains("#") && !line.trim().equals("")) {//Skip if line is a comment or is blank
                    Pattern pattern = Pattern.compile(hostnameRegex);//Only look for hostnames in a string
                    Matcher matcher = pattern.matcher(line);//Apply the pattern to the string
                    if (matcher.find()) {//Check if the string meets our requirements
                        String hostname = matcher.group();
                        if(hostname.contains(".") && hostname.length() >= 4 && !hostname.equals("www.")) {//Extra checks
                            out.add(matcher.group());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public void showAlert(String title, String message, int icon) {
        JOptionPane.showMessageDialog(null, message, title, icon);
    }

}
