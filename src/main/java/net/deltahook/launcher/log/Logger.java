package net.deltahook.launcher.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final String prefix;

    public Logger() {
        this.prefix = null;
    }

    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    private void info(String string) {
        String time = String.format("[%s] ", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        String prefix = (this.prefix == null ? "" : "["+this.prefix+"] ");
        System.out.println(time + prefix + string);
    }
}
