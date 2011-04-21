package net.sourceforge.sheltermanager.asm.startup;

import java.io.File;


/** This class starts the HSQLDB manager on the local database */
public class HSQLManager {
    public static void main(String[] args) {
        // First and only arg should be installation directory
        // with a trailing slash.
        if (args.length == 0) {
            System.out.println("Missing install dir argument.");
            System.exit(1);
        }

        String d = args[0];

        // Strip quotes
        if (d.startsWith("\"")) {
            d = d.substring(1);
        }

        if (d.endsWith("\"")) {
            d = d.substring(0, d.length() - 1);
        }

        // Add slash
        if (!d.endsWith(File.separator)) {
            d += File.separator;
        }

        // Construct exec args
        StringBuffer s = new StringBuffer();

        boolean isWin = (System.getProperty("os.name").toLowerCase()
                               .indexOf("windows") != -1);

        // Base exe
        String javabin = (isWin ? "javaw.exe" : "java");
        String exepath = System.getProperty("java.home") + File.separator +
            "bin" + File.separator + javabin;

        if (isWin) {
            exepath = "\"" + exepath + "\"";
        }

        s.append(exepath);

        // RAM
        s.append(" -Xmx256m");

        // Classpath
        s.append(" -cp " + (isWin ? "\"" : "") + d + "lib" + File.separator +
            "hsqldb.jar" + (isWin ? "\"" : ""));

        // Bootclass
        s.append(" org.hsqldb.util.DatabaseManager ");

        // User
        s.append("-user sa ");

        // URL
        String url = "jdbc:hsqldb:file:" + System.getProperty("user.home") +
            File.separator + ".asm" + File.separator + "localdb";
        s.append("-url " + (isWin ? "\"" : "") + url + (isWin ? "\"" : ""));

        // Fire it off
        try {
            System.out.println("Executing: " + s.toString());
            Runtime.getRuntime().exec(s.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
