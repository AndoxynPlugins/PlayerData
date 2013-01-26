package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * This is a class to Parse lines into
 *
 * @author daboross
 */
final class FileParser {

    /**
     *
     * @param file
     * @param name
     * @return
     * @throws IllegalArgumentException
     */
    protected static PData parseList(ArrayList<String> file, String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name Can't Be Null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File Can't Be Null");
        }
        String userName = name;
        String nickName = "";
        ArrayList<Long> logIns = new ArrayList<Long>();
        ArrayList<Long> logOuts = new ArrayList<Long>();
        Map<String, ArrayList<String>> dataMap = new HashMap<String, ArrayList<String>>();
        long timePlayed = 0;//This is the data to store from the timePlayed field;
        String current = "finding";
        for (int i = 0; i < file.size(); i++) {
            String currentString = file.get(i);
            char[] currentCharList = currentString.toCharArray();
            if (current.equalsIgnoreCase("finding")) {
                if (currentCharList.length > 0) {
                    if (currentCharList[currentCharList.length - 1] == ':') {
                        current = currentString.substring(0, (currentString.length() - 1)).toLowerCase();
                    } else {
                        PlayerData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data File!!! User Name: {0} Line: {1}", new Object[]{userName, i});
                        return null;
                    }
                }
            } else if (currentString.length() < 1) {
                current = "finding";
            } else {
                if (current.equalsIgnoreCase("nickname")) {
                    nickName = currentString;
                } else if (current.equalsIgnoreCase("timeplayed")) {
                    try {
                        timePlayed = Long.valueOf(currentString);
                    } catch (Exception e) {
                        PlayerData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!!! Name: {0} line: {1}", new Object[]{userName, i});
                    }
                } else if (current.equalsIgnoreCase("logins")) {
                    try {
                        logIns.add(Long.valueOf(currentString));
                    } catch (Exception e) {
                        PlayerData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!!! Name: {0} line: {1}", new Object[]{userName, i});
                    }
                } else if (current.equalsIgnoreCase("logouts")) {
                    try {
                        logOuts.add(Long.valueOf(currentString));
                    } catch (Exception e) {
                        PlayerData.getCurrentInstance().getLogger().log(Level.SEVERE, "Error Parsing Player Data!!! Name: {0} line: {1}", new Object[]{userName, i});
                    }
                } else {
                    if (!dataMap.containsKey(current)) {
                        dataMap.put(current, new ArrayList<String>());
                    }
                    dataMap.get(current).add(currentString);
                }
            }
        }
        ArrayList<Data> parsedDataList = new ArrayList<Data>();
        for (String str : dataMap.keySet()) {
            Data currentD = new Data(str, dataMap.get(str).toArray(new String[0]));
            parsedDataList.add(currentD);
        }
        Data[] returnData = parsedDataList.toArray(new Data[0]);
        PData pData = new PData(userName, nickName, logIns, logOuts, timePlayed, returnData);
        return pData;
    }

    /**
     * Get a list of strings for a file that represents this Player
     *
     * @param pData The Player Data to parse to the list.
     * @return A list that can be stored in a file then parsed into a PData
     * again using parseFromList
     */
    protected static ArrayList<String> parseToList(PData pData) {
        if (pData == null) {
            throw new IllegalArgumentException("PData Can't Be Null");
        }
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("nickname:");
        lines.add(pData.nickName(false));
        lines.add("");
        lines.add("logins:");
        Long[] logIns = pData.logIns();
        for (int i = 0; i < logIns.length; i++) {
            lines.add(logIns[i].toString());
        }
        lines.add("");
        lines.add("logouts:");
        Long[] logOuts = pData.logOuts();
        for (int i = 0; i < logOuts.length; i++) {
            lines.add(logOuts[i].toString());
        }
        lines.add("");
        lines.add("timeplayed:");
        lines.add(String.valueOf(pData.timePlayed()));
        lines.add("");
        for (Data d : pData.getData()) {
            lines.add(d.getName() + ":");
            lines.addAll(Arrays.asList(d.getData()));
            lines.add("");
        }
        return lines;
    }
}
