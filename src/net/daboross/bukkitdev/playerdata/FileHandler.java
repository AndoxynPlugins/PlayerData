package net.daboross.bukkitdev.playerdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.Bukkit;

/**
 * This class contains various helpful functions
 */
final class FileHandler {

    /**
     * Writes a file with text lines to filePath/fileName
     *
     * @param file this is the file to write to
     * @param lines this is the text to put in the file
     * @return True if successful, false otherwise
     */
    protected static boolean WriteFile(File file, ArrayList<String> lines) {
        Bukkit.getServer().getLogger().log(Level.FINEST, "Player Data File Handler: Writing New File: {0}", file.getAbsolutePath());
        if (lines == null || file == null) {
            return false;
        }
        if (file.canWrite() || !file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
                    for (int i = 0; i < lines.size(); i++) {
                        bf.write(lines.get(i));
                        bf.newLine();
                    }
                    return true;
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Reads a file into an array list of strings
     *
     * @param file this is the file to read from
     * @return The text in the file, or null if it doesn't exist
     */
    protected static ArrayList<String> ReadFile(File file) {
        ArrayList<String> lines = new ArrayList</*String*/>();
        if (file.canRead()) {
            FileReader fr = null;
            BufferedReader bf = null;
            try {
                fr = new FileReader(file);
                bf = new BufferedReader(fr);
                while (true) {
                    String line = bf.readLine();
                    if (line == null) {
                        break;
                    }
                    lines.add(line);
                }
            } catch (Exception e) {
            }
            try {
                bf.close();
            } catch (Exception e) {
            }
            try {
                fr.close();
            } catch (Exception e) {
            }
        }
        return lines;
    }

    /**
     * This reads a file from within the .jar to an ArrayList of strings
     *
     * @param filePath this is the location of the file within the jar archive.
     * starts with a /
     * @return an ArrayList of strings that is the text of the file
     */
    protected static ArrayList<String> ReadInternalFile(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        InputStream is = FileHandler.class
                .getResourceAsStream(filePath);
        if (is != null) {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr);
                while (true) {
                    String line = bf.readLine();
                    if (line == null) {
                        break;
                    }
                    lines.add(line);
                }
                bf.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return lines;
    }
}
