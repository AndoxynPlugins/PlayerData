/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.parsers.XMLFileParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.helpers.FirstJoinComparator;
import net.daboross.bukkitdev.playerdata.helpers.LastSeenComparator;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLException;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * This is the internal handler of all PlayerDataImpl. This class is the network
 * that holds all the other functioning objects and classes together. When the
 * server starts up, it will go through all the files in the playerdata folder,
 * and read each one with the FileHandler. Then get a PlayerDataImpl from the
 * BPDFileParser, and put that PlayerDataImpl into its internal list. It stores
 * all the PDatas in two lists. All the PDatas are in the playerDataList. Then
 * they are also either in the aliveList or the deadList. When a PlayerDataImpl
 * is created, it will ask the PlayerHandlerImpl to put it in either the
 * aliveList or the dead List.
 *
 * @author daboross
 */
public final class PlayerHandlerImpl implements PlayerHandler {

    private final Object playerDataListLock = new Object();
    /**
     * This is a list of all the PDatas loaded. This list should contain one
     * PlayerDataImpl for EVERY player who has EVER joined the server.
     */
    private final ArrayList<PlayerDataImpl> playerDataList = new ArrayList<PlayerDataImpl>();
    private final ArrayList<PlayerDataImpl> playerDataListFirstJoin = new ArrayList<PlayerDataImpl>();
    private final PlayerDataBukkit playerDataMain;
    private final File dataFolder;

    /**
     * Use this to create a new PlayerHandlerImpl when PlayerDataBukkit is
     * loaded. There should only be one PlayerHandlerImpl instance.
     */
    protected PlayerHandlerImpl(PlayerDataBukkit playerDataMain) {
        this.playerDataMain = playerDataMain;
        File pluginFolder = playerDataMain.getDataFolder();
        if (pluginFolder != null) {
            dataFolder = new File(pluginFolder, "xml");
            if (dataFolder != null) {
                if (!dataFolder.isDirectory()) {
                    dataFolder.mkdirs();
                }
            }
        } else {
            dataFolder = null;
            playerDataMain.getLogger().severe("Plugin Data Folder Is Null!");
        }
    }

    /**
     * This function creates a PlayerDataImpl for every player who has ever
     * joined this server. It uses Bukkit's store of players and their data. It
     * will only load the first getDate a player has played and the last getDate
     * they have played from this function. This WILL erase all data currently
     * stored by PlayerDataBukkit. This WILL return before the data is loaded.
     *
     * @return The number of new PlayerDataImpl Files created.
     */
    protected int createEmptyPlayerDataFilesFromBukkit() {
        OfflinePlayer[] pls = Bukkit.getServer().getOfflinePlayers();
        return createEmptyPlayerDataFiles(pls);
    }

    /**
     * This creates an empty PlayerDataImpl for every OfflinePlayer in this
     * list. This WILL erase all data currently recorded on any players included
     * in this list. If any of the players have not played on this server
     * before, then they are not included. This WILL return before the data is
     * loaded.
     *
     * @return The number of players loaded from this list.
     */
    protected int createEmptyPlayerDataFiles(OfflinePlayer[] players) {
        int returnValue = 0;
        synchronized (playerDataListLock) {
            playerDataList.clear();
            playerDataListFirstJoin.clear();
            for (int i = 0; i < players.length; i++) {
                if (players[i].hasPlayedBefore()) {
                    PlayerDataImpl pData = new PlayerDataImpl(players[i]);
                    if (!playerDataList.contains(pData)) {
                        playerDataList.add(pData);
                    }
                    if (!playerDataListFirstJoin.contains(pData)) {
                        playerDataListFirstJoin.add(pData);
                    }
                    returnValue += 1;
                }
            }
        }
        saveAllData(false, null);
        loadDataFromFiles(null);
        return returnValue;
    }

    /**
     * This function Goes through all PDatas who are online, and tells them
     * their player has logged out, which in turn saves all unsaved PDatas. This
     * function doesn't save any offline player's PDatas, because there is no
     * way for their state to change after the player has logged out, and they
     * auto save when their player logs out. The only reason this function is
     * helpful is if the PlayerDataBukkit Plugin is unloaded while the server is
     * still running.
     */
    protected void endServer() {
        Player[] ls = Bukkit.getOnlinePlayers();
        for (Player p : ls) {
            PlayerDataImpl pData = getPlayerData(p);
            pData.loggedOut(p, this);
        }
    }

    /**
     * This function goes through all online player's PDatas and tells each of
     * them that the Player has logged in. The only reason this function is
     * helpful is if the PlayerDataBukkit Plugin is loaded when the server is
     * already running and there are players online.
     */
    private void startServer() {
        Player[] ls = Bukkit.getOnlinePlayers();
        for (Player p : ls) {
            PlayerDataImpl pData = getPlayerData(p);
            pData.loggedIn(p, this);
        }
    }

    public void saveAllData(final boolean executeAsync, final Callable<Void> callAfter) {
        if (executeAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(playerDataMain, new Runnable() {
                @Override
                public void run() {
                    synchronized (playerDataListLock) {
                        for (PlayerDataImpl pData : playerDataList) {
                            pData.updateStatus();
                            savePDataXML(pData);
                        }
                        if (callAfter != null) {
                            Bukkit.getScheduler().callSyncMethod(playerDataMain, callAfter);
                        }
                    }
                }
            });
        } else {
            for (PlayerDataImpl pData : playerDataList) {
                pData.updateStatus();
                savePDataXML(pData);
            }
            if (callAfter != null) {
                try {
                    callAfter.call();
                } catch (Exception ex) {
                    Logger.getLogger(PlayerHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * This function saves the given PlayerDataImpl to file. This should ONLY be
     * run from within the PlayerDataImpl class. If you want to manually save a
     * PlayerDataImpl from outside that PlayerDataImpl's object, then run that
     * PlayerDataImpl's update method, with parameters (true,true).
     */
    protected void savePData(PlayerDataImpl pData) {
        if (pData == null) {
            return;
        }
        synchronized (playerDataListLock) {
            if (!playerDataList.contains(pData)) {
                playerDataList.add(0, pData);
            }
            if (!playerDataListFirstJoin.contains(pData)) {
                playerDataListFirstJoin.add(pData);
            }
        }
        savePDataXML(pData);
    }

    private void savePDataXML(PlayerDataImpl pd) {
        File file = new File(dataFolder, pd.getUsername() + ".xml");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            playerDataMain.getLogger().log(Level.SEVERE, "Exception Creating New File", ex);
        }
        try {
            XMLFileParser.writeToFile(pd, file);
        } catch (DXMLException ex) {
            playerDataMain.getLogger().log(Level.SEVERE, "Exception Writing To File", ex);
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
    @Override
    public String getFullUsername(String username) {
        if (username == null) {
            throw new NullArgumentException("Username Can't Be Null");
        }
        //This is a list of usernames to return, in order from first choice to last choise
        String[] returnUserNames = new String[12];
        String user = ChatColor.stripColor(username).toLowerCase();
        synchronized (playerDataListLock) {
            for (int i = 0; i < playerDataList.size(); i++) {
                PlayerDataImpl pD = playerDataList.get(i);
                String checkUserName = pD.getUsername().toLowerCase();
                String checkNickName = ChatColor.stripColor(pD.getDisplayname()).toLowerCase();
                String pUserName = pD.getUsername();
                int add = pD.isOnline() ? 0 : 1;
                if (checkUserName != null) {
                    if (checkUserName.equalsIgnoreCase(user)) {
                        if (returnUserNames[0] == null) {
                            returnUserNames[0] = pUserName;
                        }
                        break;
                    }
                    if (checkUserName.startsWith(user)) {
                        if (returnUserNames[4 + add] == null) {
                            returnUserNames[4 + add] = pUserName;
                        }
                    }
                    if (checkUserName.contains(user)) {
                        if (returnUserNames[8 + add] == null) {
                            returnUserNames[8 + add] = pUserName;
                        }
                    }
                    if (checkNickName != null) {
                        if (checkNickName.equalsIgnoreCase(user)) {
                            if (returnUserNames[2 + add] == null) {
                                returnUserNames[2 + add] = pUserName;
                            }
                        }
                        if (checkNickName.startsWith(user)) {
                            if (returnUserNames[6 + add] == null) {
                                returnUserNames[6 + add] = pUserName;
                            }
                        }
                        if (checkNickName.contains(user)) {
                            if (returnUserNames[10 + add] == null) {
                                returnUserNames[10 + add] = pUserName;
                            }
                        }
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
        ArrayList<String> onlineUserNames = new ArrayList<String>();//This is online player's usernames
        ArrayList<String> onlineNickNames = new ArrayList<String>();//This is online player's nicknames
        ArrayList<String> pUserNames = new ArrayList<String>();//This is offline player's usernames
        ArrayList<String> pNickNames = new ArrayList<String>();//This is offline player's nicknames
        int onlineNumberFound = 0;
        int offlineNumberFound = 0;
        String user = ChatColor.stripColor(userName).toLowerCase();
        synchronized (playerDataListLock) {
            for (int i = 0; i < playerDataList.size(); i++) {
                final PlayerDataImpl pData = playerDataList.get(i);
                final boolean online = pData.isOnline();
                final String pUserName = pData.getUsername();
                final String pNickName = pData.getDisplayname();
                final String checkUserName = pUserName.toLowerCase();
                final String checkNickName = ChatColor.stripColor(pNickName).toLowerCase();
                if (checkUserName != null) {
                    if (checkUserName.equalsIgnoreCase(pNickName)) {
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
        }
        String[] returnList = new String[onlineNumberFound + offlineNumberFound];
        for (int i = 0; i < onlineNumberFound && i < returnList.length; i++) {
            if (onlineNickNames.get(i) == null) {
                returnList[i] = onlineUserNames.get(i);
            } else {
                returnList[i] = onlineUserNames.get(i) + ColorList.DIVIDER + "/" + onlineNickNames.get(i);
            }
        }
        for (int i = 0, k = onlineNumberFound; i < offlineNumberFound && k < returnList.length; i++, k++) {
            returnList[k] = (pNickNames.get(i) == null) ? pUserNames.get(i) : pUserNames.get(i) + ColorList.DIVIDER + "/" + pNickNames.get(i);

        }
        return returnList;
    }

    /**
     * This gets a PlayerDataImpl from a given username. The usernames needs to
     * be an exact match of the PlayerDataImpl's recorded username, not case
     * sensitive. If you want to find the exact username given a partial
     * username, then use the getFullUsername() function.
     *
     * @param name The FULL username of a player in the database.
     * @return The PlayerDataImpl that is loaded for that player, or null if not
     * found.
     */
    @Override
    public PlayerDataImpl getPlayerData(String name) {
        if (name == null) {
            return null;
        }
        synchronized (playerDataListLock) {
            for (int i = 0; i < playerDataList.size(); i++) {
                if (playerDataList.get(i).getUsername().equalsIgnoreCase(name)) {
                    return playerDataList.get(i);
                }
            }
        }
        return null;
    }

    @Override
    public PlayerData getPlayerDataPartial(String partialName) {
        return getPlayerData(getFullUsername(partialName));
    }

    /**
     * This function gets a PlayerDataImpl given an online Player. This function
     * just goes through all loaded PDatas and sees if any of their names
     * exactly equals the given Player's name.
     *
     * @return The PlayerDataImpl loaded for the given Player. Or null if the
     * Player Given is null.
     */
    @Override
    public PlayerDataImpl getPlayerData(Player p) {
        if (p == null) {
            return null;
        }
        synchronized (playerDataListLock) {
            for (int i = 0; i < playerDataList.size(); i++) {
                PlayerDataImpl pData = playerDataList.get(i);
                if (pData.getUsername().equalsIgnoreCase(p.getName())) {
                    return pData;
                }
            }
            PlayerDataImpl pData = new PlayerDataImpl(p);
            if (!playerDataList.contains(pData)) {
                playerDataList.add(pData);
            }
            if (!playerDataListFirstJoin.contains(pData)) {
                playerDataListFirstJoin.add(pData);
            }
            return pData;
        }
    }

    /**
     * This will log in a given player's PlayerDataImpl.
     *
     * @return a PlayerDataImpl for the player
     */
    public PlayerDataImpl logIn(Player p) {
        if (p == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        synchronized (playerDataListLock) {
            for (int i = 0; i < playerDataList.size(); i++) {
                PlayerDataImpl pData = playerDataList.get(i);
                if (pData.getUsername().equals(p.getName())) {
                    pData.loggedIn(p, this);
                    return pData;
                }
            }
            PlayerDataImpl pData = new PlayerDataImpl(p);
            pData.loggedIn(p, this);
            if (!playerDataList.contains(pData)) {
                playerDataList.add(0, pData);
            }
            if (!playerDataListFirstJoin.contains(pData)) {
                playerDataListFirstJoin.add(pData);
            }
            return pData;
        }
    }

    public PlayerData logOut(Player p) {
        PlayerDataImpl pData = getPlayerData(p);
        pData.loggedOut(p, this);
        return pData;
    }

    /**
     * This function gives all custom data loaded of a given data Type. This
     * function goes through ALL loaded PDatas and checks each one if they have
     * data of the given type. The returned list is created in this command and
     * no references are kept. You can do whatever you want to to the list.
     *
     * @param dataName The type of the data.
     */
    @Override
    public List<PlayerData> getAllPlayerDatasWithExtraData(String dataName) {
        synchronized (playerDataListLock) {
            List<PlayerData> returnArrayList = new ArrayList<PlayerData>();
            for (PlayerDataImpl pData : playerDataList) {
                if (pData.hasExtraData(dataName)) {
                    returnArrayList.add(pData);
                }
            }
            return returnArrayList;
        }
    }

    /**
     * This function gets all PDatas loaded, which should be one for each Player
     * who has ever joined the server. This returns an unmodifiable list.
     *
     * @return A copy of the list of PDatas that PlayerHandlerImpl keeps.
     */
    @Override
    public List<PlayerDataImpl> getAllPlayerDatas() {
        synchronized (playerDataListLock) {
            return Collections.unmodifiableList(playerDataList);
        }
    }

    /**
     * This returns an unmodifiable list!
     */
    public List<PlayerDataImpl> getAllPDatasFirstJoin() {
        return Collections.unmodifiableList(playerDataListFirstJoin);
    }

    /**
     * This Function moves the PlayerDataImpl given to the top of the list.
     * Should be only called BY THE PDATA when the player has logged in.
     */
    void loggedIn(PlayerDataImpl pd) {
        synchronized (playerDataListLock) {
            while (playerDataList.contains(pd)) {
                playerDataList.remove(pd);
            }
            playerDataList.add(0, pd);
        }
    }

    /**
     * This Function moves the PlayerDataImpl down the list. Should be only
     * called BY THE PDATA when the player has logged in.
     */
    void loggedOut(PlayerDataImpl pd) {
        synchronized (playerDataListLock) {
            int pos = playerDataList.indexOf(pd);
            while (playerDataList.contains(pd)) {
                playerDataList.remove(pd);
            }
            while (pos < playerDataList.size() && playerDataList.get(pos).isOnline()) {
                pos++;
            }
            playerDataList.add(pos, pd);
        }
    }

    /**
     * This will Sort the PlayerDataImpl lists depending on how long it has been
     * since each played last joined. IN A SEPERATE THREAD.
     */
    public void sortData(Runnable afterLoad) {
        Runnable sorter = new Sorter(afterLoad);
        Bukkit.getScheduler().runTaskAsynchronously(playerDataMain, sorter);

    }

    @Override
    public List<? extends PlayerData> getAllPlayerDatasFirstJoin() {
        throw new UnsupportedOperationException("PlayerHandlerImpl: Not Created Yet!: getAllPlayerDatasFirstJoin");
    }

    private class Sorter implements Runnable {

        private final Runnable afterLoad;

        public Sorter(Runnable afterLoad) {
            this.afterLoad = afterLoad;
        }

        @Override
        public void run() {
            synchronized (playerDataListLock) {
                Collections.sort(playerDataList, LastSeenComparator.getInstance());
                Collections.sort(playerDataListFirstJoin, FirstJoinComparator.getInstance());
            }
            if (afterLoad != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(playerDataMain, afterLoad);
            }
        }
    }

    private void loadDataFromFiles(final Runnable runAfter) {
        final Logger l = playerDataMain.getLogger();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                readData(l, runAfter);
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(playerDataMain, run);
    }

    private void readData(final Logger l, final Runnable runAfter) {
        readData(l);
        Bukkit.getScheduler().scheduleSyncDelayedTask(playerDataMain, runAfter);
    }

    /**
     * This is the "initial" function that should be called directly after this
     * PlayerHandlerImpl is created. The PlayerHandlerImpl instance variable in
     * PlayerDataBukkit needs to be set to this PlayerHandlerImpl before this
     * function is called. This will also create new PDatas from Bukkit if file
     * folder is empty.
     */
    protected void init() {
        final Logger l = playerDataMain.getLogger();
        if (dataFolder.listFiles().length == 0) {
            createEmptyPlayerDataFilesFromBukkit();
        }
        readData(l);
        synchronized (playerDataListLock) {
            Collections.sort(playerDataList, LastSeenComparator.getInstance());
            Collections.sort(playerDataListFirstJoin, FirstJoinComparator.getInstance());
        }
        startServer();
    }

    /**
     * This function removes all the current PDatas loaded, and loads new ones
     * from the files in the playerdata folder. This function should only be
     * used on startup.
     */
    private void readData(Logger l) {
        int count = loadAllPDataToList();
        l.log(Level.INFO, "Read {0} Player Data Files", count);
    }

    private int loadAllPDataToList() {
        synchronized (playerDataListLock) {
            playerDataList.clear();
            playerDataListFirstJoin.clear();
            if (dataFolder != null && dataFolder.exists()) {
                File[] playerFiles = dataFolder.listFiles();
                for (File fl : playerFiles) {
                    if (fl != null) {
                        if (fl.canRead()) {
                            if (fl.isFile()) {
                                String type = fl.getName().substring(fl.getName().lastIndexOf('.') + 1, fl.getName().length());
                                if (type.equals("xml")) {
                                    PlayerDataImpl pData = null;
                                    try {
                                        pData = XMLFileParser.readFromFile(fl);
                                    } catch (DXMLException dxmle) {
                                        playerDataMain.getLogger().log(Level.SEVERE, "Exception While Reading: " + fl.getAbsolutePath(), dxmle);
                                    }
                                    if (pData != null) {
                                        if (!playerDataList.contains(pData)) {
                                            playerDataList.add(pData);
                                        }
                                        if (!playerDataListFirstJoin.contains(pData)) {
                                            playerDataListFirstJoin.add(pData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return playerDataList.size();
            } else {
                return 0;
            }
        }
    }

    public int numPlayersLoaded() {
        return playerDataList.size();
    }
}
