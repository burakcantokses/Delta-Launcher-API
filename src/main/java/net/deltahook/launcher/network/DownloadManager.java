package net.deltahook.launcher.network;

import net.deltahook.launcher.bootstrap.Bootstrap;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DownloadManager {
    private static final File deltaFolder = new File(Bootstrap.PATH_DELTA_FOLDER);
    private static final File deltaNatives = new File(Bootstrap.PATH_NATIVES_FOLDER);
    private static final File deltaJar = new File(Bootstrap.PATH_DELTA_JAR);
    private static final String githubLink = "http://some.random.github.link/path/to/the/text.txt"; //A github link that points to the download link and checksum of the zip file
    private static final File temporaryDownloadFolder = new File("C:\\ProgramData\\DeltaClientCache");
    private static final File clientZIP = new File(temporaryDownloadFolder, "DeltaClient.zip");

    public static boolean isDeltaInstalled() {
        checkSubscription();
        if(!deltaFolder.exists() || !deltaNatives.exists() || !deltaJar.exists()) return false;
        //Take hashes of files and check (if needed)
        return true;
    }

    public static void checkSubscription() {
        //Check subscription
        if(false) {
            System.exit(-2);
        }
    }

    public static void install() throws Exception {
        downloadZIP();
        extractAndSetUpFiles();
    }

    private static void downloadZIP() throws Exception {
        if(clientZIP.exists()) {
            if(getSHA1(clientZIP).equalsIgnoreCase(getZIPChecksum())) return;
            clientZIP.delete();
        }
        BufferedInputStream in = new BufferedInputStream(new URL(getDownloadLink()).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(clientZIP);
        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
    }

    private static void extractAndSetUpFiles() throws Exception {
        //Need to delete all client related stuff before setting up
        deleteRecursively(deltaFolder);

        //Use decryption key to decrypt zip entries
        //Extract them and move to the corresponding places
        ZipFile zipFile = new ZipFile(clientZIP); //Use a library to handle zip extracting stuff
        InputStream in = zipFile.getInputStream(new ZipEntry("Delta Client"));
        FileOutputStream fos = new FileOutputStream(deltaFolder);
        byte[] buffer = new byte[1024];
        while(in.read(buffer) != -1) {
            fos.write(buffer);
        }
        fos.flush();
        fos.close();
    }

    private static String getDownloadLink() throws Exception {
        URL url = new URL(githubLink);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        return reader.readLine();
    }

    private static String getZIPChecksum() throws Exception {
        URL url = new URL(githubLink);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        reader.readLine();
        return reader.readLine();
    }

    private static String getSHA1(File file) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            return new HexBinaryAdapter().marshal(sha1.digest());
        }
    }

    private static String getDecryptionKey() { //There should be a key that only customers have, otherwise everyone that have launcher would decrypt the zip file
        return "";
    }

    private static void deleteRecursively(File file) {
        if(file.isDirectory())
            for(File f : file.listFiles())
                deleteRecursively(f);

        file.delete();
    }
}
