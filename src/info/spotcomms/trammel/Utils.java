/*
 * Copyright (c) 2015. Divested Computing Group
 */

package info.spotcomms.trammel;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
class Utils {

    String ipAddressRegex = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";//Credit: http://stackoverflow.com/a/5667402
    //String hostnameRegex = "(?:(?:(?:(?:[a-zA-Z0-9][-a-zA-Z0-9]{0,61})?[a-zA-Z0-9])[.])*(?:[a-zA-Z][-a-zA-Z0-9]{0,61}[a-zA-Z0-9]|[a-zA-Z])[.]?)";//Credit: http://stackoverflow.com/a/1418724
    private static final String hostnameRegex = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";//Credit: http://www.mkyong.com/regular-expressions/domain-name-regular-expression-example/

    //Credit: http://stackoverflow.com/a/4895572
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte aB : b)
            result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    //Credit: http://fahdshariff.blogspot.ru/2011/08/java-7-deleting-directory-by-walking.html
    public void deleteDirectory(Path dir) {
        try {
            System.gc();
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
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
            showAlert("Hosts Manager", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void downloadFile(String url, Path out, boolean useCache) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(45000);
            connection.setReadTimeout(45000);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0) Gecko/20100101 Firefox/19.0");
            if (useCache && out.toFile().exists()) {
                connection.setIfModifiedSince(out.toFile().lastModified());
            }
            connection.connect();
            int res = connection.getResponseCode();
            if (res != 304 && (res == 200 || res == 301 || res == 302)) {
                Files.copy(connection.getInputStream(), out, StandardCopyOption.REPLACE_EXISTING);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hosts Manager", "Failed to download " + url, JOptionPane.ERROR_MESSAGE);
        }
    }

    public File getConfigDir() {
        File configDir = new File("");
        switch (getOS()) {
            case "Linux":
                configDir = new File("/etc/trammel/");
                break;
            case "Mac":
                configDir = new File(System.getProperty("user.home") + "/Library/Application Support/Trammel/");
                break;
            case "Windows":
                configDir = new File(System.getenv("AppData") + "/Trammel/");
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

    private String getOS() {
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
            showAlert("Hosts Manager", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        return "Unknown";
    }

    public String identifyFileType(String url) {
        String extension = ".txt";
        if (url.contains("zip"))
            extension = ".zip";
        else if (url.contains("gz"))
            extension = ".gz";
        else if (url.contains("7zip"))
            extension = ".7z";
        else if (url.contains("7z"))
            extension = ".7z";
        return extension;
    }

    //Credit: http://stackoverflow.com/a/23538961
    private static boolean isAdmin() {
        try {
            Preferences prefs = Preferences.systemRoot();
            prefs.put("trammel", "swag");//SecurityException on Windows
            prefs.remove("trammel");
            prefs.flush();//BackingStoreException on Linux
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<String> readFileIntoArray(File in) {
        ArrayList<String> out = new ArrayList<>();
        try {
            Scanner fileIn = new Scanner(in);
            while (fileIn.hasNext()) {
                String line = fileIn.nextLine();
                if (!line.startsWith("#"))
                    out.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hosts Manager", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        return out;
    }

    public ArrayList<String> readHostsFileIntoArray(File in) {
        ArrayList<String> out = new ArrayList<>();
        try {
            Scanner fileIn = null;
            if (identifyFileType(in.toString()).contains(".txt")) {//Plain text
                fileIn = new Scanner(in);
            }
            if (identifyFileType(in.toString()).contains(".7z")) {//Decompress 7z
                RandomAccessFile newFile = new RandomAccessFile(in, "r");
                IInArchive compressedList = SevenZip.openInArchive(null, new RandomAccessFileInStream(newFile));
                ISimpleInArchive compressedListNew = compressedList.getSimpleInterface();
                for (ISimpleInArchiveItem file : compressedListNew.getArchiveItems()) {
                    String fileName = file.getPath();
                    if (fileName.contains("/")) {
                        fileName = fileName.split("/")[fileName.split("/").length - 1];
                    }
                    if (fileName.equalsIgnoreCase("hosts") || fileName.startsWith("hosts") || fileName.startsWith("HOSTS") || fileName.startsWith("Hosts") || fileName.equals("Hosts.rsk") || fileName.equals("Hosts.pub") || fileName.equals("Hosts.trc")) {
                        File newFileOutPath = new File(getConfigDir() + "/tmp/" + "7ztmp");
                        newFileOutPath.mkdirs();
                        if (newFileOutPath.exists()) {
                            System.gc();
                            newFileOutPath.delete();
                        }
                        ExtractOperationResult result = file.extractSlow(data -> {
                            try {
                                FileOutputStream newFileOut = new FileOutputStream(newFileOutPath);
                                newFileOut.write(data);
                                newFileOut.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return data.length;
                        });
                        fileIn = new Scanner(newFileOutPath);
                        break;
                    }
                }
            }
            if (identifyFileType(in.toString()).contains(".zip")) {//Decompress ZIP
                //Credit: http://stackoverflow.com/a/14656534 and http://stackoverflow.com/a/18974782
                ZipFile compressedList = new ZipFile(in);
                ArrayList compressedFiles = (ArrayList) compressedList.getFileHeaders();
                for (Object file : compressedFiles) {
                    FileHeader newFile = (FileHeader) file;
                    if (newFile.getFileName().equalsIgnoreCase("hosts") || newFile.getFileName().startsWith("hosts") || newFile.getFileName().startsWith("HOSTS") || newFile.getFileName().startsWith("Hosts")) {
                        fileIn = new Scanner(compressedList.getInputStream(newFile));
                    }
                }
            }
            if (identifyFileType(in.toString()).contains(".gz")) {//Decompress GunZip
                fileIn = new Scanner(new GZIPInputStream(new FileInputStream(in)));
            }
            int c = 0;
            while (fileIn.hasNext()) {
                String line = fileIn.nextLine().toLowerCase();
                if (!line.startsWith("#") && !line.trim().equals("")) {//Skip if line is a comment or is blank
                    Pattern pattern = Pattern.compile(hostnameRegex);//Only look for hostnames in a string
                    line = line.replaceAll(".*\\://", "").replaceAll("/", "");
                    String[] spaceSplit = line.replaceAll("\\s", "~").split("~");
                    Matcher matcher;
                    for (String aSpaceSplit : spaceSplit) {
                        matcher = pattern.matcher(aSpaceSplit);//Apply the pattern to the string
                        if (matcher.find()) {//Check if the string meets our requirements
                            out.add(matcher.group());
                            c++;
                        } else if (aSpaceSplit.contains("xn--")) {
                            out.add(aSpaceSplit);//Sssssh, its okay
                            c++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hosts Manager", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        return out;
    }

    public void showAlert(String title, String message, int icon) {
        JOptionPane.showMessageDialog(null, message, title, icon);
    }

}
