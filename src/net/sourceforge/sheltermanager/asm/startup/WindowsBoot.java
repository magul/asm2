package net.sourceforge.sheltermanager.asm.startup;


/** This class boots ASM on Windows machines using javaw.exe - This is to get
 * around the stupid number of characters limitation in Windows shortcuts
 * (god I hate Microsoft)
 */
public class WindowsBoot {
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
        if (!d.endsWith("\\")) {
            d += "\\";
        }

        // Construct exec args
        StringBuffer s = new StringBuffer();

        // Base exe
        s.append("\"" + d + "java\\bin\\javaw.exe" + "\" ");

        // Lib dir
        // s.append("\"-Djava.library.path=" + d + "lib\" ");

        // Max RAM
        String ram = System.getProperty("asm.ram", "256");
        s.append("-Xmx" + ram + "m ");

        // Classpath
        s.append("-cp \"" + d + "asm.jar;" + d + "lib\\charting-0.94.jar;" + d +
            "lib\\mysql.jar;" + d + "lib\\postgresql.jar;" + d +
            "lib\\hsqldb.jar\" ");

        // Bootclass
        s.append("net.sourceforge.sheltermanager.asm.startup.Startup ");

        // Data dir
        s.append(" \"" + d + "data" + "\"");

        // Fire it off
        try {
            System.out.println("Executing: " + s.toString());
            Runtime.getRuntime().exec(s.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
