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
    private final PlayerDataBukkit playerDataBukkit;
    private final File dataFolder;

    /**
     * Use this to create a new PlayerHandlerImpl when PlayerDataBukkit is
     * loaded. There should only be one PlayerHandlerImpl instance.
     */
    protected PlayerHandlerImpl(PlayerDataBukkit playerDataMain) {
        this.playerDataBukkit = playerDataMain;
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
     * This function erases the entire database and creates a PlayerData for
     * every player who has ever joined this server.
     *
     * @return The number of new PlayerData Files created.
     */
    protected int createEmptyPlayerDataFilesFromBukkit() {
        OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
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
                }
            }
        }
        saveAllData(false, null);
        readDataToList();
        return playerDataList.size();
    }

    /**
     * This function Goes through all PlayerDatas who are online, and tells them
     * their player has logged out, which in turn saves all unsaved PlayerDatas.
     */
    protected void endServer() {
        Player[] ls = Bukkit.getOnlinePlayers();
        for (Player p : ls) {
            PlayerDataImpl pData = getPlayerData(p);
            pData.loggedOut(p, this);
        }
    }

    public void saveAllData(final boolean executeAsync, final Callable<Void> callAfter) {
        if (executeAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(playerDataBukkit, new Runnable() {
                @Override
                public void run() {
                    synchronized (playerDataListLock) {
                        for (PlayerDataImpl pData : playerDataList) {
                            pData.updateStatus();
                            savePDataXML(pData);
                        }
                        if (callAfter != null) {
                            Bukkit.getScheduler().callSyncMethod(playerDataBukkit, callAfter);
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
        savePDataXML(pData);
    }

    private void savePDataXML(PlayerDataImpl pd) {
        File file = new File(dataFolder, pd.getUsername() + ".xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                playerDataBukkit.getLogger().log(Level.SEVERE, "Exception creating new file " + file.getAbsolutePath(), ex);
                return;
            }
        }
        if (file.canWrite()) {
            try {
                XMLFileParser.writeToFile(pd, file);
            } catch (DXMLException ex) {
                playerDataBukkit.getLogger().log(Level.SEVERE, "Exception saving data to file " + file.getAbsolutePath(), ex);
            }
        } else {
            playerDataBukkit.getLogger().log(Level.SEVERE, "Can''t write to file {0}", file.getAbsolutePath());
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
    public String getFullUsername(String partialName) {
        if (partialName == null) {
            throw new NullArgumentException("Username can't be null");
        }
        String[] possibleMatches = new String[2];
        String partialNameLowerCase = partialName.toLowerCase();
        synchronized (playerDataListLock) {
            for (int i = 0; i < playerDataList.size(); i++) {
                PlayerData pd = playerDataList.get(i);
                String checkUserName = pd.getUsername().toLowerCase();
                String checkNickName = ChatColor.stripColor(pd.getDisplayname()).toLowerCase();
                if (checkUserName.equals(partialNameLowerCase)) {
                    return pd.getUsername();
                }
                if (checkUserName.contains(partialNameLowerCase)) {
                    if (possibleMatches[0] == null) {
                        possibleMatches[0] = pd.getUsername();
                    }
                }
                if (checkNickName.contains(partialNameLowerCase)) {
                    if (possibleMatches[1] == null) {
                        possibleMatches[1] = pd.getUsername();
                    }
                }
            }
        }
        for (int i = 0; i < possibleMatches.length; i++) {
            if (possibleMatches[i] != null) {
                return possibleMatches[i];
            }
        }
        return null;
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
        Bukkit.getScheduler().runTaskAsynchronously(playerDataBukkit, sorter);

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
                Bukkit.getScheduler().scheduleSyncDelayedTask(playerDataBukkit, afterLoad);
            }
        }
    }

    /**
     * This is the "initial" function that should be called directly after this
     * PlayerHandlerImpl is created. The PlayerHandlerImpl instance variable in
     * PlayerDataBukkit needs to be set to this PlayerHandlerImpl before this
     * function is called. This will also create new PDatas from Bukkit if file
     * folder is empty.
     */
    protected void init() {
        if (dataFolder.listFiles().length == 0) {
            createEmptyPlayerDataFilesFromBukkit();
        }
        readDataToList();
        synchronized (playerDataListLock) {
            Collections.sort(playerDataList, LastSeenComparator.getInstance());
            Collections.sort(playerDataListFirstJoin, FirstJoinComparator.getInstance());
        }
        Player[] ls = Bukkit.getOnlinePlayers();
        for (Player p : ls) {
            PlayerDataImpl pData = getPlayerData(p);
            pData.loggedIn(p, this);
        }
    }

    private int readDataToList() {
        synchronized (playerDataListLock) {
            playerDataList.clear();
            playerDataListFirstJoin.clear();
            int count = 0;
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
                                        playerDataBukkit.getLogger().log(Level.SEVERE, "Exception While Reading: " + fl.getAbsolutePath(), dxmle);
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
                count = playerDataList.size();
            }
            playerDataBukkit.getLogger().log(Level.INFO, "Read {0} data files", count);
            return count;
        }
    }
}
