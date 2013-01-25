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
 * This is the internal handler of all PData. This class is the network that
 * holds all the other functioning objects and classes together. When the server
 * starts up, it will go through all the files in the playerdata folder, and
 * read each one with the FileHandler. Then get a PData from the FileParser, and
 * put that PData into its internal list. It stores all the PDatas in two lists.
 * All the PDatas are in the playerDataList. Then they are also either in the
 * aliveList or the deadList. When a PData is created, it will ask the
 * PDataHandler to put it in either the aliveList or the dead List.
 *
 * @author daboross
 */
final class PDataHandler {

    /**
     * This is a list of all the PDatas loaded. This list should contain one
     * PData for EVERY player who has EVER joined the server.
     */
    private final ArrayList<PData> playerDataList = new ArrayList<>();
    /**
     * This is a list of all "alive" PDatas loaded. This list should contain one
     * PData for EVERY player who has joined the server in the past 2 months. A
     * PData will tell whether the PDataHandler should include it in the alive
     * list or the dead list when it is loaded.
     */
    private final ArrayList<PData> aliveList = new ArrayList<>();
    /**
     * This is a list of all "dead" PDatas loaded. This list should contain
     * every PData that is not in the "alive" list.
     */
    private final ArrayList<PData> deadList = new ArrayList<>();
    private PlayerData playerDataMain;
    private File playerDataFolder;
    private Map<String, DataDisplayParser> ddpMap = new HashMap<>();

    /**
     * Use this to create a new PDataHandler when PlayerData is loaded. There
     * should only be one PDataHandler instance.
     */
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

    /**
     * This is the "initial" function that should be called directly after this
     * PDataHandler is created. The PDataHandler instance variable in PlayerData
     * needs to be set to this PDataHandler before this function is called.
     */
    protected void init() {
        readData();
    }

    /**
     * This function creates a PData for every player who has ever joined this
     * server. It uses Bukkit's store of players and their data. It will only
     * load the first time a player has played and the last time they have
     * played from this function. This WILL erase all data currently stored by
     * PlayerData.
     *
     * @return The number of new PData Files created.
     */
    protected int createEmptyPlayerDataFilesFromBukkit() {
        OfflinePlayer[] pls = Bukkit.getServer().getOfflinePlayers();
        return createEmptyPlayerDataFiles(pls);
    }

    /**
     * This creates an empty PData for every OfflinePlayer in this list. This
     * WILL erase all data currently recorded on any players included in this
     * list. If any of the players have not played on this server before, then
     * they are not included.
     *
     * @return The number of players loaded from this list.
     */
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
        readData();
        return returnValue;
    }

    /**
     * This function removes all the current PDatas loaded, and loads new ones
     * from the files in the playerdata folder. This function should only be
     * used on startup.
     */
    private void readData() {
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

    /**
     * This function Goes through all PDatas who are online, and tells them
     * their player has logged out, which in turn saves all unsaved PDatas. This
     * function doesn't save any offline player's PDatas, because there is no
     * way for their state to change after the player has logged out, and they
     * auto save when their player logs out. The only reason this function is
     * helpful is if the PlayerData Plugin is unloaded while the server is still
     * running.
     */
    protected void endServer() {
        Player[] ls = Bukkit.getServer().getOnlinePlayers();
        for (Player p : ls) {
            PData pData = getPData(p);
            pData.loggedOut();
        }
    }

    /**
     * This function goes through all online player's PDatas and tells each of
     * them that the Player has logged in. The only reason this function is
     * helpful is if the PlayerData Plugin is loaded when the server is already
     * running and there are players online.
     */
    protected void startServer() {
        Player[] ls = Bukkit.getServer().getOnlinePlayers();
        for (Player p : ls) {
            PData pData = getPData(p);
            pData.loggedIn();
        }
    }

    /**
     * This function goes through ALL loaded PDatas and forces each of them to
     * save.
     */
    protected void saveAllData() {
        for (int i = 0; i < playerDataList.size(); i++) {
            PData pData = playerDataList.get(i);
            if (pData != null) {
                pData.updateStatus(false, false);
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

    /**
     * This function saves the given PData to file. This should ONLY be run from
     * within the PData class. If you want to manually save a PData from outside
     * that PData's object, then run that PData's update method, with parameters
     * (true,true).
     */
    protected void savePData(PData pData) {
        if (pData == null) {
            return;
        }
        if (!playerDataList.contains(pData)) {
            playerDataList.add(pData);
        }
        String name = pData.userName();
        if (name != null) {
            File pFile = new File(playerDataFolder, (name + ".bpd"));
            ArrayList<String> playerFileLines = FileParser.parseToList(pData);
            if (playerFileLines == null || playerFileLines.isEmpty()) {
                playerDataMain.getLogger().severe("File Parser Has Given Invalid Line List!");
            } else {
                FileHandler.WriteFile(pFile, playerFileLines);
            }
        }
    }

    /**
     * This function gets the full username from a partial username given. The
     * way this function works is by going through all usernames loaded, which
     * should be all players who have ever played on the server, and checks if
     * their username contains the given string, or if their last display name
     * contains the given string. Will return the BEST match, not the first one.
     * The best match is determined in this order:
     *
     * First priority is if the given string equals (ignoring case) a loaded
     * username.
     *
     * Second priority is if the given string equals (ignoring case and colors)
     * a loaded nickname.
     *
     * Third priority is if the a loaded username begins with the given string.
     *
     * Fourth priority is if the a loaded displayname begins with the given
     * string.
     *
     * Fifth priority is if a loaded username contains the given string.
     *
     * And finally sixth priority is if a loaded nickname contains the given
     * string.
     *
     * ALSO, People who are online always have priority over people who are
     * offline. And people who have joined within the last 2 months have
     * priority of people who haven't.
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

    /**
     * This function gets a list of Player's who's usernames or nicknames
     * contain the given String. They are ordered in a priority, as Follows:
     *
     * First priority is if the given string equals (ignoring case) a loaded
     * username.
     *
     * Second priority is if the given string equals (ignoring case and colors)
     * a loaded nickname.
     *
     * Third priority is if the a loaded username begins with the given string.
     *
     * Fourth priority is if the a loaded displayname begins with the given
     * string.
     *
     * Fifth priority is if a loaded username contains the given string.
     *
     * And finally sixth priority is if a loaded nickname contains the given
     * string.
     *
     * ALSO, People who are online always have priority over people who are
     * offline. And people who have joined within the last 2 months have
     * priority of people who haven't.
     */
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
     * This gets a PData from a given username. The usernames needs to be an
     * exact match of the PData's recorded username, not case sensitive. If you
     * want to find the exact username given a partial username, then use the
     * getFullUsername() function.
     *
     * @param name The FULL username of a player in the database.
     * @return The PData that is loaded for that player, or null if not found.
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

    /**
     * This function gets a PData given an online Player. This function just
     * goes through all loaded PDatas and sees if any of their names exactly
     * equals the given Player's name.
     *
     * @return The PData loaded for the given Player. Or null if the Player
     * Given is null.
     */
    protected PData getPData(Player p) {
        PlayerData.getCurrentInstance().getLogger().log(Level.FINE, "Getting PData From Player: {0}", p.getName());
        if (p == null) {
            return null;
        }
        for (int i = 0; i < playerDataList.size(); i++) {
            PData pData = playerDataList.get(i);
            if (pData.userName().equalsIgnoreCase(p.getName())) {
                pData.setPlayer(p);
                return pData;
            }
        }
        PData pData = new PData(p);
        playerDataList.add(pData);
        return pData;
    }

    /**
     * This function sets whether the given PData is in the alive list or the
     * dead list. This function should ONLY be called from the PData class. And
     * should ALWAYS be called when a PData is created.
     */
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

    /**
     * Adds a DataDisplayParser that you supply as a parser for a custom data
     * type you specify. PlayerData will call the shortInfo() function from your
     * display parser and include the lines your parser returns whenever someone
     * uses /playerdata viewinfo for a player with this data type. You will need
     * to run this function every time your Plugin is loaded because PlayerData
     * will not keep the DataDisplayParser after unload. This will overwrite any
     * previous DataDisplayParsers loaded with this function for this Data Type.
     *
     * @param dataName The Name of the data this parser will parse. If you have
     * multiple data types that this parser can parse, then you will need to run
     * this function once for each data type.
     * @param ddp The Data Display Parser that will parse the data given.
     */
    protected void addDataParser(String name, DataDisplayParser ddp) {
        ddpMap.put(name, ddp);
    }

    /**
     * This function gets the "Displayable Data" for a given Custom Data. This
     * function checks to see if there are any custom data parsers loaded for
     * the given datas type, and if there are, it will call that Data Display
     * Parser's method for getting displayable data. If no Data Display Parser
     * is loaded for this data type, then it will return an array of Strings,
     * with 0 strings in it.
     *
     * @param d The data to parse.
     * @param longInfo Whether to call the DataDisplayParser's LongInfo. If
     * false, then the ShortInfo method is called.
     */
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

    /**
     * This function gives all custom data loaded of a given data Type. This
     * function goes through ALL loaded PDatas and checks each one if they have
     * data of the given type.
     *
     * @param dataName The type of the data.
     */
    protected Data[] getAllData(String dataName) {
        ArrayList<Data> returnArrayList = new ArrayList<>();
        for (PData pData : playerDataList) {
            if (pData.hasData(dataName)) {
                returnArrayList.add(pData.getData(dataName));
            }
        }
        return returnArrayList.toArray(new Data[0]);
    }

    /**
     * This function gets all PDatas loaded, which should be one for each Player
     * who has ever joined the server. This function returns a copy of the list
     * that PDataHandler keeps, but each of the PDatas in that list is the same
     * PData that is loaded in PDataHandler.
     *
     * @return A copy of the list of PDatas that PDataHandler keeps.
     */
    protected PData[] getAllPDatas() {
        return playerDataList.toArray(new PData[0]);
    }
}
