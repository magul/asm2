/*
 Animal Shelter Manager
 Copyright(c)2000-2009, R. Rawson-Tetley

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTIBILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston
 MA 02111-1307, USA.

 Contact me by electronic mail: bobintetley@users.sourceforge.net
 */
package net.sourceforge.sheltermanager.dbfs;

import net.sourceforge.sheltermanager.asm.globals.*;
import net.sourceforge.sheltermanager.cursorengine.*;

import java.io.*;


/**
 * Encodes a virtual file system in a single database table. Uses Base64 to
 * encode file contents.
 *
 * @author Robin Rawson-Tetley
 */
public class DBFS {
    private String currentPath = null;

    /**
     * Creates a new instance of DBFS. Create a DBFS when you want to move
     * around the virtual DB filesystem. The new instance will start in /
     */
    public DBFS() {
        currentPath = "/";
    }

    /**
     * @return the current path.
     */
    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * Sets the path to the new path.
     *
     * @throws DBFSException
     *             if the path does not exist
     */
    public void setCurrentPath(String path) throws DBFSException {
        try {
            // Root always exists
            if (path.equals("/")) {
                currentPath = "/";

                return;
            }

            String name = path.substring(path.lastIndexOf("/") + 1,
                    path.length());
            String pathbit = path.substring(0, path.lastIndexOf("/"));

            if (path.lastIndexOf("/") == 0) {
                pathbit = "/";
            }

            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT Name FROM dbfs WHERE Path Like '" +
                pathbit + "' AND Name Like '" + name + "' AND Content Is Null",
                "dbfs");

            if (rs.getEOF()) {
                throw new DBFSException("Path does not exist: " + path);
            }

            rs.free();
            rs = null;
        } catch (Exception e) {
            throw new DBFSException(e);
        }

        currentPath = path;
    }

    /**
     * @return The list of files in the current directory
     */
    public String[] list() {
        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT Name FROM dbfs WHERE Path Like '" +
                currentPath + "'", "dbfs");

            if (rs.getEOF()) {
                return new String[0];
            }

            String[] l = new String[(int) rs.getRecordCount()];
            int i = 0;

            while (!rs.getEOF()) {
                l[i] = (String) rs.getField("Name");
                rs.moveNext();
                i++;
            }

            rs.free();
            rs = null;

            return l;
        } catch (Exception e) {
            Global.logException(e, DBFS.class);

            return null;
        }
    }

    /**
     * @param mask
     *            A mask to use
     * @return The list of files in the current directory
     */
    public String[] list(String mask) {
        try {
            SQLRecordset rs = new SQLRecordset();
            mask.replace('*', '%');
            rs.openRecordset("SELECT Name FROM dbfs WHERE Path Like '" +
                currentPath + "' AND Name Like '" + mask + "'", "dbfs");

            if (rs.getEOF()) {
                return new String[0];
            }

            String[] l = new String[(int) rs.getRecordCount()];
            int i = 0;

            while (!rs.getEOF()) {
                l[i] = (String) rs.getField("Name");
                rs.moveNext();
                i++;
            }

            rs.free();
            rs = null;

            return l;
        } catch (Exception e) {
            Global.logException(e, DBFS.class);

            return null;
        }
    }

    /**
     * Tests if the given file in the current directory is itself a directory. A
     * directory is a file entry with NULL contents
     *
     * @param file
     *            The file to check
     * @return true if the file (in the current directory) is itself a directory
     */
    public boolean isDir(String file) {
        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT Name FROM dbfs WHERE Path Like '" +
                currentPath + "' AND Name Like '" + file +
                "' AND Content Is Null", "dbfs");

            boolean isEOF = rs.getEOF();
            rs.free();
            rs = null;

            return !isEOF;
        } catch (Exception e) {
            Global.logException(e, DBFS.class);
        }

        return false;
    }

    /**
     * Returns the given file loaded into a string. Handy if your file is ASCII,
     * like HTML, source code, etc.
     *
     * @param file
     *            The file to read
     * @return A string containing the file contents
     */
    public String readFileToString(String file) throws DBFSException {
        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT Content FROM dbfs WHERE Path Like '" +
                currentPath + "' AND Name Like '" + file + "'", "dbfs");

            if (rs.getEOF()) {
                throw new DBFSException("File does not exist: " + file);
            }

            String content = (String) rs.getField("Content");

            rs.free();
            rs = null;

            return new String(Base64.decode(content));
        } catch (Exception e) {
            throw new DBFSException(e);
        }
    }

    /**
     * Reads the given file from the current directory, decodes it, saves it to
     * the temp directory and returns a file link to it.
     *
     * @param file
     *            The file to read
     * @return A File reference to the file on the hard disk
     * @throws DBFSException
     *             if the named file doesn't exist
     */
    public File readFile(String file) throws DBFSException {
        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT Content FROM dbfs WHERE Path Like '" +
                currentPath + "' AND Name Like '" + file + "'", "dbfs");

            if (rs.getEOF()) {
                throw new DBFSException("File does not exist: " + file);
            }

            String ofile = Global.tempDirectory + File.separator +
                rs.getField("Name");
            byte[] filecontent = Base64.decode(rs.getField("Content").toString()
                                                 .toCharArray());
            File f = new File(ofile);
            FileOutputStream o = new FileOutputStream(f);
            o.write(filecontent);
            o.flush();
            o.close();

            rs.free();
            rs = null;

            return f;
        } catch (Exception e) {
            throw new DBFSException("Fault writing to file", e);
        }
    }

    /**
     * Reads the given file from the current directory, decodes it and
     * saves it to the path/name specified by saveto
     *
     * @param file
     *            The file to read
     * @param saveto
     *            The file to save to
     * @return A File reference to the file on the hard disk
     * @throws DBFSException
     *             if the named file doesn't exist
     */
    public void readFile(String file, String saveto) throws DBFSException {
        try {
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT Content FROM dbfs WHERE Path Like '" +
                currentPath + "' AND Name Like '" + file + "'", "dbfs");

            if (rs.getEOF()) {
                throw new DBFSException("File does not exist: " + file);
            }

            byte[] filecontent = Base64.decode(rs.getField("Content").toString()
                                                 .toCharArray());
            File f = new File(saveto);
            FileOutputStream o = new FileOutputStream(f);
            o.write(filecontent);
            o.flush();
            o.close();

            rs.free();
            rs = null;
        } catch (Exception e) {
            throw new DBFSException(e);
        }
    }

    /**
     * Uploads a file from the file system to the current directory with the
     * name given.
     */
    public void putFile(String file, String localfilename)
        throws DBFSException {
        try {
            // Try to delete any file of that name first
            deleteFile(file);

            String contents = new String(Base64.encode(getBytesFromFile(
                            new File(localfilename))));
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT * FROM dbfs WHERE ID=0", "dbfs");
            rs.addNew();
            rs.setField("ID", new Integer(DBConnection.getPrimaryKey("dbfs")));
            rs.setField("Path", currentPath);
            rs.setField("Name", file);
            rs.setField("Content", contents);
            rs.save(false, "");

            rs.free();
            rs = null;
            contents = null;
        } catch (Exception e) {
            throw new DBFSException(e);
        }
    }

    /**
     * Stores a file from the file system in the current directory. It is base
     * 64 encoded and stored in the database.
     *
     * @param file
     *            The file to upload
     * @throws DBFSException
     *             if the source file doesn't exist
     */
    public void putFile(File f) throws DBFSException {
        try {
            String contents = new String(Base64.encode(getBytesFromFile(f)));
            SQLRecordset rs = new SQLRecordset();
            rs.openRecordset("SELECT * FROM dbfs WHERE ID=0", "dbfs");
            rs.addNew();
            rs.setField("ID", new Integer(DBConnection.getPrimaryKey("dbfs")));
            rs.setField("Path", currentPath);
            rs.setField("Name", f.getName());
            rs.setField("Content", contents);
            rs.save(false, "");

            contents = null;
            rs.free();
            rs = null;
        } catch (Exception e) {
            throw new DBFSException(e);
        }
    }

    /**
     * Saves all image files (*.jpg, *.gif, *.tif, *.png) from the current
     * directory to the target Dir.
     */
    public void saveAllImages(String targetDir) {
        if (!targetDir.endsWith(File.separator)) {
            targetDir += File.separator;
        }

        String[] f = list();

        for (int i = 0; i < f.length; i++) {
            if (f[i].toLowerCase().endsWith(".jpg") ||
                    f[i].toLowerCase().endsWith(".jpeg") ||
                    f[i].toLowerCase().endsWith(".gif") ||
                    f[i].toLowerCase().endsWith(".png") ||
                    f[i].toLowerCase().endsWith(".tif") ||
                    f[i].toLowerCase().endsWith(".tiff")) {
                try {
                    readFile(f[i], targetDir + f[i]);
                } catch (Exception e) {
                    Global.logException(e, DBFS.class);
                }
            }
        }

        f = null;
    }

    /**
     * Deletes a file from the file system in the current directory.
     *
     * @param file
     *            The file to delete
     */
    public void deleteFile(String file) {
        try {
            String sql = "DELETE FROM dbfs WHERE Path Like '" + currentPath +
                "' AND Name Like '" + file + "'";
            DBConnection.executeAction(sql);
            sql = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Deletes a directory and all its contents from the current directory.
     *
     * @param dir The dir to delete
     */
    public void deleteDir(String dir) {
        try {
            // Remove contents of dir
            String sql = "DELETE FROM dbfs WHERE Path Like '" + currentPath +
                "/" + dir + "%'";
            DBConnection.executeAction(sql);
            sql = null;

            // Remove the directory entry itself
            deleteFile(dir);
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Changes to the given dir from the current directory if the
     * path is relative (does not start with /) or goes directly
     * to the directory given if an absolute path is specified.
     *
     * @param dir
     *            The directory to change into or .. to go back one
     * @throws DBFSException
     *             if the dir doesn't exist
     */
    public void chdir(String dir) throws DBFSException {
        dir = dir.trim();

        // Absolute paths
        if (dir.startsWith("/")) {
            // Remove trailing slash
            if (dir.endsWith("/")) {
                dir = dir.substring(0, dir.length() - 1);
            }

            setCurrentPath(dir);

            return;
        }

        // Relative paths
        if (dir.equals("..")) {
            // If there's only one dir between us and root, go to root
            if (currentPath.lastIndexOf("/") == 0) {
                setCurrentPath("/");

                return;
            } else {
                setCurrentPath(currentPath.substring(0,
                        currentPath.lastIndexOf("/")));

                return;
            }
        } else {
            if (currentPath.equals("/")) {
                setCurrentPath(currentPath + dir);

                return;
            } else {
                setCurrentPath(currentPath + "/" + dir);

                return;
            }
        }
    }

    /** @return true if the given file/dir exists in the current path */
    public boolean exists(String file) {
        try {
            String[] l = list(file);

            return l.length != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a directory in the current one with the given name.
     */
    public void mkdir(String dir) {
        try {
            // Verify it doesn't already exist
            if (exists(dir)) {
                return;
            }

            String sql = "INSERT INTO dbfs (ID, Path, Name, Content) VALUES (" +
                DBConnection.getPrimaryKey("dbfs") + ", " + "'" + currentPath +
                "', " + "'" + dir + "', " + "Null)";
            DBConnection.executeAction(sql);
            sql = null;
        } catch (Exception e) {
            Global.logException(e, getClass());
        }
    }

    /**
     * Removes a directory from the current one and all it's contents.
     *
     * @param dir
     *            The directory to remove
     * @throws DBFSException
     *             if the directory does not exist
     */
    public void rmdir(String dir) throws DBFSException {
        try {
            String sql = "DELETE FROM dbfs WHERE Path Like '" + currentPath +
                "/" + dir + "'";
            DBConnection.executeAction(sql);
            sql = "DELETE FROM dbfs WHERE Path Like '" + currentPath +
                "' AND Name Like '" + dir + "'";
            DBConnection.executeAction(sql);
            sql = null;
        } catch (Exception e) {
            throw new DBFSException(e);
        }
    }

    /**
     * @return true if there are no DBFS entries.
     */
    public static boolean isEmpty() {
        try {
            return DBConnection.executeForCount("SELECT COUNT(*) FROM dbfs") == 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Destroys the DBFS and imports it entirely from the contents of the
     * directory given.
     *
     * @param startDirectory
     *            The directory to start in. This directory will not be
     *            reflected in the DBFS - only it's contents.
     */
    public static void importFromFileSystem(File startDirectory) {
        try {
            // Destroy existing contents
            String sql = "DELETE FROM dbfs";
            DBConnection.executeAction(sql);

            Global.logInfo("Import DBFS from filesystem: " +
                startDirectory.getAbsolutePath(), "DBFS.importFromFileSystem");

            // Recurse the contents of the directory
            fsImport(startDirectory, new DBFS());

            // Run checkpoint
            DBConnection.checkpoint();

            Global.logInfo("Finished import.", "DBFS.importFromFileSystem");
        } catch (Exception e) {
            Global.logException(e, DBFS.class);
        }
    }

    /**
     * Imports a directory from the filesystem into the
     * current directory
     * @param directory The directory to import
     */
    public void importDirectory(File directory) {
        try {
            Global.logInfo("Import directory from filesystem: " +
                directory.getAbsolutePath(), "DBFS.importDirectory");

            // Try to remove any existing dbfs dir of that name first
            deleteDir(directory.getName());

            // Make the new dbfs dir and change into it
            mkdir(directory.getName());
            chdir(directory.getName());

            // Recurse the contents of the directory and import
            fsImport(directory, this);

            // Run checkpoint
            DBConnection.checkpoint();

            Global.logInfo("Finished import.", "DBFS.importDirectory");
        } catch (Exception e) {
            Global.logException(e, DBFS.class);
        }
    }

    /**
     * Exports a directory from the DBFS to the filesystem
     * @param fsDirectory The directory to save the DBFS folder in
     * @param directory The DBFS directory to export
     */
    public void exportDirectory(String fsDirectory, String directory) {
        try {
            Global.logInfo("Export directory to filesystem: " + directory,
                "DBFS.exportDirectory");

            // Recurse the contents of the directory
            fsExport(fsDirectory, directory, this);

            Global.logInfo("Finished export.", "DBFS.exportDirectory");
        } catch (Exception e) {
            Global.logException(e, DBFS.class);
        }
    }

    /**
     * Recursive function that reads the DBFS directory given and saves the
     * files to the filesystem, creating folders where necessary
     * @param directoryToSave the directory we're saving files to
     * @param directory the DBFS directory
     * @param dbfs A new DBFS object, pointing to the right location
     */
    private static void fsExport(String directoryToSave, String directory,
        DBFS dbfs) {
        try {
            // Make the target directory on the filesystem
            directoryToSave += (File.separator + directory);
            new File(directoryToSave).mkdirs();

            // Change into the target dbfs directory
            dbfs.chdir(directory);

            // Get a list of the contents of the DBFS dir
            String[] f = dbfs.list();

            for (int i = 0; i < f.length; i++) {
                // If it's a directory, recurse and do it
                if (dbfs.isDir(f[i])) {
                    Global.logDebug("Exporting dir: " + f[i], "DBFS.fsExport");

                    // Create a new DBFS object 
                    DBFS dbfs2 = new DBFS();
                    dbfs2.setCurrentPath(dbfs.getCurrentPath());

                    // Export the files from that directory
                    fsExport(directoryToSave, f[i], dbfs2);
                    dbfs2 = null;
                } else {
                    // Download the file
                    Global.logDebug("Downloading file: " + f[i], "DBFS.fsExport");
                    dbfs.readFile(f[i], directoryToSave + File.separator +
                        f[i]);
                }
            }

            f = null;
        } catch (Exception e) {
            Global.logException(e, DBFS.class);
        }
    }

    /**
     * Recursive function that reads the directory given and generates entries
     * in the DBFS
     */
    private static void fsImport(File sd, DBFS dbfs) {
        try {
            File[] f = sd.listFiles();

            for (int i = 0; i < f.length; i++) {
                // If it's a directory, make one in the DBFS
                // and then recursively add files
                if (f[i].isDirectory()) {
                    Global.logDebug("Importing dir: " + f[i].getAbsolutePath(),
                        "DBFS.importFromFileSystem");
                    dbfs.mkdir(f[i].getName());

                    // Create a new DBFS object to manage the changed into dir
                    DBFS dbfs2 = new DBFS();
                    dbfs2.setCurrentPath(dbfs.getCurrentPath());
                    dbfs2.chdir(f[i].getName());

                    // Import the files from that directory
                    fsImport(f[i], dbfs2);
                    dbfs2 = null;
                } else {
                    // Upload the file
                    Global.logDebug("Uploading file: " +
                        f[i].getAbsolutePath(), "DBFS.importFromFileSystem");
                    dbfs.putFile(f[i]);
                }
            }

            f = null;
        } catch (Exception e) {
            Global.logException(e, DBFS.class);
        }
    }

    /**
     * Reads a file and returns it as a byte array
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;

        while ((offset < bytes.length) &&
                ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " +
                file.getName());
        }

        is.close();

        return bytes;
    }
}
