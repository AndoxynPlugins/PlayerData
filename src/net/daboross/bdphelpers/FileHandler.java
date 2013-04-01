package net.daboross.bdphelpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.Bukkit;

/**
 * This class provides Functions to read and write to and from Files.
 */
public final class FileHandler {

    /**
     * Writes a file with text lines to a given File.
     *
     * @param file This is the File to write to.
     * @param lines This is the text to put in a file.
     * @return True if successful, False otherwise.
     */
    public static boolean WriteFile(File file, ArrayList<String> lines) {
        if (lines == null || file == null) {
            return false;
        }
        if (file.canWrite() || !file.exists()) {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
                    return false;
                }
            }
            FileWriter fw;
            try {
                fw = new FileWriter(file);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
                return false;
            }
            BufferedWriter bf = new BufferedWriter(fw);
            for (int i = 0; i < lines.size(); i++) {
                try {
                    bf.write(lines.get(i));
                    bf.newLine();
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
                    return false;
                }
            }
            try {
                bf.close();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
            }
            try {
                fw.close();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
            }
            return true;
        }
        return false;
    }

    /**
     * Reads a given file into an array list of strings
     *
     * @param file This is the file to read from
     * @return The text in the file, or null if it doesn't exist.
     */
    public static ArrayList<String> ReadFile(File file) {
        ArrayList<String> lines = new ArrayList<String>();
        if (file.canRead()) {
            FileReader fr = null;
            BufferedReader bf;
            try {
                fr = new FileReader(file);
            } catch (FileNotFoundException ex) {
                Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
            }
            if (fr != null) {
                bf = new BufferedReader(fr);
                while (true) {
                    String line;
                    try {
                        line = bf.readLine();
                    } catch (IOException ex) {
                        Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
                        break;
                    }
                    if (line == null) {
                        break;
                    }
                    lines.add(line);
                }
                try {
                    bf.close();
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.FINER, "Player Data File Handler Exception!", ex);
                }
            }
        }
        return lines;
    }
}
