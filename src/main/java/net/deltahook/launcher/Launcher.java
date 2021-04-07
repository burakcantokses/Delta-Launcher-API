package net.deltahook.launcher;

import net.deltahook.launcher.bootstrap.Bootstrap;
import net.deltahook.launcher.log.Logger;

public class Launcher {
    static Logger logger = new Logger("Delta Launcher");

    public static void main(String[] args) throws Exception {
//        LangEngine.setLang(LangEngine.Lang.TURKISH);
        logger.info("Launcher started!");
        ArgumentParser.parseArgs(args);
        Bootstrap.checkInstallation();
        Bootstrap.startGame();
    }
}
