package net.daboross.bukkitdev.playerdata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
final class PDataHandler {

    private final ArrayList<PData> playerDataList = new ArrayList<>();
    private final ArrayList<PData> aliveList = new ArrayList<>();
    private final ArrayList<PData> deadList = new ArrayList<>();
    private PlayerData playerDataMain;
    private File playerDataFolder;
    private Map<String, DataDisplayParser> ddpMap = new HashMap<>();

    protected PDataHandler(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
        File pluginFolder = playerDataMain.getDataFolder();
        if (pluginFolder != null) {
            playerDataFolder = new File(pluginFolder, "playerData");
            if (playerDataFolder != null) {
                if (!playerDataFolder.isDirectory()) {
                    playerDataFolder.mkdirs();
                }
            }
        } else {
            playerDataMain.getLogger().severe("Plugin Data Folder Is Null!");
        }
    }

    protected void init() {
        readData();
    }

    protected int createEmptyPlayerDataFilesFromBukkit() {
        OfflinePlayer[] pls = Bukkit.getServer().getOfflinePlayers();
        return createEmptyPlayerDataFiles(pls);
    }

    protected int createEmptyPlayerDataFiles(OfflinePlayer[] players) {
        int returnValue = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].hasPlayedBefore()) {
                PData pData = new PData(players[i]);
                if (!playerDataList.contains(pData)) {
                    playerDataList.add(pData);
                }
                returnValue += 1;
            }
        }
        saveAllData();
        return returnValue;
    }

    /**
     * Warning: This will remove all current PData objects and get all new ones
     * from files.
     */
    protected void readData() {
        playerDataList.clear();
        if (playerDataFolder != null) {
            File[] playerFiles = playerDataFolder.listFiles();
            for (File fl : playerFiles) {
                if (fl != null) {
                    if (fl.canRead()) {
                        String type = fl.getName().substring(fl.getName().indexOf('.') + 1, fl.getName().length());
                        if (type.equals("bpd")) {
                            ArrayList<String> fileContents = FileHandler.ReadFile(fl);
                            String name = fl.getName().substring(0, fl.getName().indexOf('.'));
                            /*When File parser parses a file, it creates a pdata, ready to return. When a PData is created, it auto adds itself to this class's playerDataList IF THE PLAYER IS ONLINE*/
                            PData pData = FileParser.parseList(fileContents, name);
                            if (!playerDataList.contains(pData) && pData != null) {
                                playerDataList.add(pData);
                            }
                        } else {
                            playerDataMain.getLogger().log(Level.INFO, "{0} file found in playerData!", type);
                        }
                    }
                }
            }
        }
        playerDataMain.getLogger().log(Level.INFO, "Loaded {0} Player Data Files", playerDataList.size());
        playerDataMain.getLogger().log(Level.INFO, "Loaded {0} Alive Player Data Files", aliveList.size());
        playerDataMain.getLogger().log(Level.INFO, "Loaded {0} Dead Player Data Files", deadList.size());
    }

    protected void endServer() {
        Player[] ls = Bukkit.getServer().getOnlinePlayers();
        for (Player p : ls) {
            PData pData = getPData(p);
            pData.loggedOut();
        }
    }

    protected void startServer() {
        Player[] ls = Bukkit.getServer().getOnlinePlayers();
        for (Player p : ls) {
            PData pData = getPData(p);
            pData.loggedIn();
        }
    }

    protected void saveAllData() {
        for (int i = 0; i < playerDataList.size(); i++) {
            PData pData = playerDataList.get(i);
            pData.updateStatus(false, false);
            if (pData != null) {
                String name = pData.userName();
                if (name != null) {
                    File pFile = new File(playerDataFolder, (name + ".bpd"));
                    ArrayList<String> playerFileLines = FileParser.parseToList(pData);
                    if (playerFileLines == null) {
                        playerDataMain.getLogger().severe("File Parser Has Given Null Refrence!");
                    } else {
                        FileHandler.WriteFile(pFile, playerFileLines);
                    }
                } else {
                    playerDataList.remove(pData);
                }
            }
        }
    }

    protected void savePData(PData pData) {
        if (pData == null) {
            return;
        }
        if (!playerDataList.contains(pData)) {
            playerDataList.add(pData);
        }
        String name = pData.userName();
        File pFile = new File(playerDataFolder, (name + ".bpd"));
        ArrayList<String> playerFileLines = FileParser.parseToList(pData);
        if (playerFileLines == null || playerFileLines.isEmpty()) {
            playerDataMain.getLogger().severe("File Parser Has Given Invalid Line List!");
        } else {
            FileHandler.WriteFile(pFile, playerFileLines);
        }

    }

    /**
     *
     * @param userName The partial username or nickname to find a full username
     * from
     * @return The full username of the person with that nickname or partial
     * username. null if username not found
     * @throws NullArgumentException if input is null.
     */
    protected String getFullUsername(String userName) {
        if (userName == null) {
            throw new NullArgumentException("UserName Can't Be Null");
        }
        //This is a list of usernames to return, in order from first choice to last choise
        String[] returnUserNames = new String[12];
        String user = ChatColor.stripColor(userName).toLowerCase();
        for (int i = 0; i < aliveList.size(); i++) {
            PData pD = aliveList.get(i);
            String checkUserName = pD.userName().toLowerCase();
            String checkNickName = ChatColor.stripColor(pD.nickName(false)).toLowerCase();
            String pUserName = pD.userName();
            int add;
            if (pD.isOnline()) {
                add = 0;
            } else {
                add = 6;
            }
            if (checkUserName != null) {
                if (checkUserName.equalsIgnoreCase(user)) {
                    returnUserNames[0 + add] = pUserName;
                    break;
                }
                if (checkUserName.startsWith(user)) {
                    returnUserNames[2 + add] = pUserName;
                }
                if (checkUserName.contains(user)) {
                    returnUserNames[4 + add] = pUserName;
                }
                if (checkNickName != null) {
                    if (checkNickName.equalsIgnoreCase(user)) {
                        returnUserNames[1 + add] = pUserName;
                    }
                    if (checkNickName.startsWith(user)) {
                        returnUserNames[3 + add] = pUserName;
                    }
                    if (checkNickName.contains(user)) {
                        returnUserNames[5 + add] = pUserName;
                    }
                }
            }
        }
        for (int i = 0; i < returnUserNames.length; i++) {
            if (returnUserNames[i] != null) {
                return returnUserNames[i];
            }
        }
        returnUserNames = new String[6];
        for (int i = 0; i < deadList.size(); i++) {
            PData pD = deadList.get(i);
            String checkUserName = pD.userName().toLowerCase();
            String checkNickName = ChatColor.stripColor(pD.nickName(false)).toLowerCase();
            String pUserName = pD.userName();
            if (checkUserName != null) {
                if (checkUserName.equalsIgnoreCase(user)) {
                    returnUserNames[0] = pUserName;
                    break;
                }
                if (checkUserName.startsWith(user)) {
                    returnUserNames[2] = pUserName;
                }
                if (checkUserName.contains(user)) {
                    returnUserNames[4] = pUserName;
                }
                if (checkNickName != null) {
                    if (checkNickName.equalsIgnoreCase(user)) {
                        returnUserNames[1] = pUserName;
                    }
                    if (checkNickName.startsWith(user)) {
                        returnUserNames[3] = pUserName;
                    }
                    if (checkNickName.contains(user)) {
                        returnUserNames[5] = pUserName;
                    }
                }
            }
        }
        for (int i = 0; i < returnUserNames.length; i++) {
            if (returnUserNames[i] != null) {
                return returnUserNames[i];
            }
        }
        return null;
    }

    protected String[] getPossibleUsernames(String userName) {
        if (userName == null) {
            throw new NullArgumentException("UserName Can't Be Null");
        }
        //This is a list of usernames to return, in order from first choice to last choise
        ArrayList<String> onlineUserNames = new ArrayList<>();//This is online player's usernames
        ArrayList<String> onlineNickNames = new ArrayList<>();//This is online player's nicknames
        ArrayList<String> pUserNames = new ArrayList<>();//This is offline player's usernames
        ArrayList<String> pNickNames = new ArrayList<>();//This is offline player's nicknames
        int onlineNumberFound = 0;
        int offlineNumberFound = 0;
        String user = ChatColor.stripColor(userName).toLowerCase();
        for (int i = 0; i < aliveList.size(); i++) {
            PData pD = aliveList.get(i);
            boolean online = pD.isOnline();
            String checkUserName = pD.userName().toLowerCase();
            String checkNickName = ChatColor.stripColor(pD.nickName(false)).toLowerCase();
            String pUserName = pD.userName();
            String pNickName = pD.nickName(false);
            if (checkUserName != null) {
                if (checkNickName == null || checkUserName.equalsIgnoreCase(checkNickName)) {
                    if (checkUserName.equalsIgnoreCase(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(null);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(null);
                            offlineNumberFound++;
                        }
                    } else if (checkUserName.startsWith(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(null);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(null);
                            offlineNumberFound++;
                        }
                    } else if (checkUserName.contains(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(null);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(null);
                            offlineNumberFound++;
                        }
                    }
                } else {
                    if (checkUserName.equalsIgnoreCase(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(pNickName);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(pNickName);
                            offlineNumberFound++;
                        }
                    } else if (checkUserName.contains(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(pNickName);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(pNickName);
                            offlineNumberFound++;
                        }
                    } else if (checkNickName.equalsIgnoreCase(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(pNickName);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(pNickName);
                            offlineNumberFound++;
                        }
                    } else if (checkNickName.startsWith(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(pNickName);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(pNickName);
                            offlineNumberFound++;
                        }
                    } else if (checkNickName.contains(user)) {
                        if (online) {
                            onlineUserNames.add(pUserName);
                            onlineNickNames.add(pNickName);
                            onlineNumberFound++;
                        } else {
                            pUserNames.add(pUserName);
                            pNickNames.add(pNickName);
                            offlineNumberFound++;
                        }
                    }
                }
            }
        }
        String[] returnList = new String[onlineNumberFound + offlineNumberFound];
        for (int i = 0; i < onlineNumberFound && i < returnList.length; i++) {
            if (onlineNickNames.get(i) == null) {
                returnList[i] = onlineUserNames.get(i);
            } else {
                returnList[i] = onlineUserNames.get(i) + ColorList.DATA_HANDLE_SLASH + "/" + onlineNickNames.get(i);
            }
        }
        int k = onlineNumberFound;
        for (int i = 0; i < offlineNumberFound && k < returnList.length; i++) {
            if (pNickNames.get(i) == null) {
                returnList[k] = pUserNames.get(i);
            } else {
                returnList[k] = pUserNames.get(i) + ColorList.DATA_HANDLE_SLASH + "/" + pNickNames.get(i);
            }
            k++;
        }
        return returnList;
    }

    /**
     *
     * @param name The FULL username of a player in the database.
     * @return
     * @throws Throwable if PData not found
     */
    protected PData getPDataFromUsername(String name) {
        PlayerData.getCurrentInstance().getLogger().log(Level.FINE, "Getting PData: {0}", name);
        for (int i = 0; i < playerDataList.size(); i++) {
            if (playerDataList.get(i).userName().equalsIgnoreCase(name)) {
                return playerDataList.get(i);
            }
        }
        return null;
    }

    protected PData getPData(Player p) {
        PlayerData.getCurrentInstance().getLogger().log(Level.FINE, "Getting PData From Player: {0}", p.getName());
        if (p == null) {
            throw new NullArgumentException("Player Can't Be Null");
        }
        for (int i = 0; i < playerDataList.size(); i++) {
            PData pData = playerDataList.get(i);
            if (pData.userName().equals(p.getName())) {
                pData.setPlayer(p);
                return pData;
            }
        }
        PData pData = new PData(p);
        playerDataList.add(pData);
        return pData;
    }

    protected void setAlive(PData pData, boolean alive) {
        if (!playerDataList.contains(pData)) {
            playerDataList.add(pData);
        }
        if (alive) {
            if (!aliveList.contains(pData)) {
                aliveList.add(pData);
            }
            if (deadList.contains(pData)) {
                deadList.remove(pData);
            }
        } else {
            if (aliveList.contains(pData)) {
                aliveList.remove(pData);
            }
            if (!deadList.contains(pData)) {
                deadList.add(pData);
            }
        }
    }

    protected void addDataParser(String name, DataDisplayParser ddp) {
        ddpMap.put(name, ddp);
    }

    protected String[] getDisplayData(Data d, boolean longInfo) {
        if (ddpMap.containsKey(d.getName())) {
            if (longInfo) {
                return ddpMap.get(d.getName()).longInfo(d);
            } else {
                return ddpMap.get(d.getName()).shortInfo(d);
            }
        }
        return new String[0];
    }

    protected Data[] getAllData(String dataName) {
        ArrayList<Data> returnArrayList = new ArrayList<>();
        for (PData pData : playerDataList) {
            if (pData.hasData(dataName)) {
                returnArrayList.add(pData.getData(dataName));
            }
        }
        return returnArrayList.toArray(new Data[0]);
    }

    protected PData[] getAllPDatas() {
        return playerDataList.toArray(new PData[0]);
    }
}
