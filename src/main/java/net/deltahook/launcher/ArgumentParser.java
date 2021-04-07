package net.deltahook.launcher;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import net.deltahook.launcher.bootstrap.Bootstrap;

import java.util.Base64;

public class ArgumentParser {

    public static void parseArgs(String... encodedArgs) throws Exception {
        if(encodedArgs == null || encodedArgs.length !=  1)
            throw new WrongNumberArgsException("Wrong number of arguments");

        byte[] encodedArr = Base64.getDecoder().decode(encodedArgs[0]);
        StringBuilder decodedArgs = new StringBuilder();
        for(byte encoded : encodedArr)
            decodedArgs.append(encoded ^ 0xDE17A);

        String[] args = decodedArgs.toString().split(";");

        if(args.length != 3)
            throw new IllegalArgumentException("Not enough arguments | Illegal arguments, needs 3, found: "+args.length);

        Bootstrap.USERNAME = args[0];
        Bootstrap.RAM_MIN = args[1];
        Bootstrap.RAM_MAX = args[2];
    }
}
