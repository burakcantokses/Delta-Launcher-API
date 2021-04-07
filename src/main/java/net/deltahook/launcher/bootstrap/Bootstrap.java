package net.deltahook.launcher.bootstrap;

import net.deltahook.launcher.network.DownloadManager;

import java.io.File;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class Bootstrap {
    public static String PATH_MINECRAFT_FOLDER;
    public static String PATH_NATIVES_FOLDER;
    public static String PATH_ASSETS_FOLDER;
    public static String PATH_DELTA_FOLDER;
    public static String PATH_DELTA_JAR;
    public static String PATH_JAVAW_EXE;

    public static String USERNAME;
    public static String RAM_MIN;
    public static String RAM_MAX;

    private static void findPaths() {
        PATH_MINECRAFT_FOLDER = System.getProperty("user.home")+"\\AppData\\Roaming\\.minecraft";
        PATH_ASSETS_FOLDER = PATH_MINECRAFT_FOLDER + "\\assets";
        PATH_DELTA_FOLDER = PATH_MINECRAFT_FOLDER + "\\versions\\Delta Client";
        PATH_NATIVES_FOLDER = PATH_DELTA_FOLDER + "\\Delta Client-natives-17329329999700";
        PATH_DELTA_JAR = PATH_DELTA_FOLDER + "\\Delta Client.jar";
        PATH_JAVAW_EXE = !isJavaInEnvironmentVariables() ? getJava() : "javaw";
    }

    public static void checkInstallation() {
        try {
            findPaths();

            File minecraftFolder = new File(PATH_MINECRAFT_FOLDER);
            if(!minecraftFolder.exists())
                throw new NoSuchFileException("Couldn't find .minecraft folder, make sure you have original minecraft installed (and maybe played 1.8.X once)");

            File assetsFolder = new File(PATH_ASSETS_FOLDER);
            if(!assetsFolder.exists())
                throw new NoSuchFileException("Couldn't find .minecraft folder, make sure you have original minecraft installed (and maybe played 1.8.X once)");

            if(PATH_JAVAW_EXE == null || !new File(PATH_JAVAW_EXE).exists())
                throw new NoSuchFileException("Couldn't find any installed Java Runtime Environment");

            if(!DownloadManager.isDeltaInstalled()) {
                DownloadManager.install(); //Throws exception if download fails, continues if everything is ok
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void startGame(){
        //Any before-start task will be added here
        String output = runCommand("cmd", "/c", getLaunchArgs());
    }

    private static String runCommand(String... commands) {
        try {
            if(commands == null)
                throw new IllegalArgumentException("");
            Process process = Runtime.getRuntime().exec(commands);
            InputStream processOutput = process.getInputStream();

            int loop = 0;
            while(processOutput.available() <= 0) {
                loop++;
                Thread.sleep(100); //Wait for any possible output
                if(loop == 10)
                    break;
            }

            byte[] output = new byte[processOutput.available()];
            if(processOutput.read(output) == -1)
                throw new Exception();
            return new String(output);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean isJavaInEnvironmentVariables() {
        String output = runCommand("cmd", "/c", "java");
        return !output.startsWith("'java' is not recognized as an internal or external command"); //"Usage: java"
    }

    private static String getJava() {
        File javaFolder = new File("C:\\Program Files\\Java");
        if(!javaFolder.exists() || !javaFolder.isDirectory()) return null;

        File[] javaVersions = javaFolder.listFiles();
        if(javaVersions == null || javaVersions.length == 0) return null;

        for(File javaVersion : javaVersions) {
            File javawEXE;
            String version = javaVersion.getName();
            if(version.contains("1.8.0")) {
                if(version.startsWith("jre")) {
                    javawEXE = new File(javaVersion, "bin\\javaw.exe");
                    return javawEXE.exists() ? javawEXE.getAbsolutePath() : null;
                } else if (version.startsWith("jdk")) {
                    javawEXE = new File(javaVersion, "jre\\bin\\javaw.exe");
                    return javawEXE.exists() ? javawEXE.getAbsolutePath() : null;
                }
            }
        }
        return null;
    }

    private static String getLaunchArgs() {
        return PATH_JAVAW_EXE
                + " -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump"
                + " -XX:+UseConcMarkSweepGC"
                + " -XX:+CMSIncrementalMode"
                + " -XX:-UseAdaptiveSizePolicy"
                + " -Xmn"+RAM_MIN+"M"
                + " -Xmx"+RAM_MAX+"M"
                + " -Djava.library.path=\""+PATH_NATIVES_FOLDER+"\""
                + " -Dminecraft.applet.TargetDirectory=\""+PATH_MINECRAFT_FOLDER+"\""
                + " -jar \""+PATH_DELTA_JAR+"\""
                + " --username \""+USERNAME+"\""
                + " --version \"Delta Client\"" //Does not matter i think
                + " --gameDir \""+PATH_MINECRAFT_FOLDER+"\""
                + " --assetsDir \""+PATH_ASSETS_FOLDER+"\""
                + " --assetIndex 1.8"
                + " --uuid 00000000-0000-0000-0000-000000000000"
                + " --accessToken null"
                + " --userProperties {}"
                + " --userType legacy";
    }
}
