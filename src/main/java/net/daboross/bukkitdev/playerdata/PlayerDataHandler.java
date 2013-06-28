package net.daboross.bukkitdev.playerdata;

import java.util.List;

/**
 * This is the main API function for PlayerData.
 *
 * @author daboross
 */
public class PlayerDataHandler {

    private final PDataHandler pDataHandler;
    private final PlayerData playerDataMain;

    /**
     * Creates A Player Data Handler given a PlayerData
     */
    public PlayerDataHandler(PlayerData playerDataMain) {
        this.pDataHandler = playerDataMain.getPDataHandler();
        this.playerDataMain = playerDataMain;
    }

    /**
     * Checks if a Player Exists in the database. DataBase should include all
     * players who have ever joined the server.
     *
     * @param name THe Name of the Player To Check
     * @return true if the player exists in the database, false otherwise.
     */
    public boolean doesPlayerExists(String name) {
        return (pDataHandler.getPDataFromUsername(name) != null);
    }

    /**
     * Get the Full UserName from a partial username or displayname. This will
     * search through a database of all players who have joined in the last 2
     * months. If Any Player's DisplayName or UserName contains the String
     * inputed, then the username of that player is returned. Not case sensitive
     *
     * @returns The Full Username of the Player Found, or Null if no player is
     * found who's name contains the given string.
     */
    public String getFullUsername(String name) {
        return pDataHandler.getFullUsername(name);
    }

    /**
     * Adds the data given to the Player given. If the player already has a data
     * with data's name, then the previous data is replaced with the new data.
     */
    public boolean addCustomData(String playerName, Data data) {
        PData pData = pDataHandler.getPDataFromUsername(playerName);
        if (pData != null) {
            pData.addData(data);
            return true;
        } else {
            return false;
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
    public void addCustomDataParsing(String dataName, DataDisplayParser ddp) {
        if (dataName == null || ddp == null) {
            throw new IllegalArgumentException("Null Argument!");
        }
        pDataHandler.addDataParser(dataName, ddp);
    }

    /**
     *
     * Get Custom Data For Given Player Name and Given data Name. Will return
     * null if player is not found or if data with given name is not found on
     * player. This internally calls the getFullUsername() on the playerName
     * given function before getting player data for that username.
     *
     * @param playerName The Name of the player to get data for
     * @param dataName The Data Type to get
     * @return The Data That PlayerData is holding for that player if this
     * dataType has been loaded onto this player. null otherwise
     */
    public Data getCustomData(String playerName, String dataName) {
        PData pData = pDataHandler.getPDataFromUsername(playerName);
        if (pData != null) {
            return pData.getData(dataName);
        } else {
            return null;
        }
    }

    /**
     * This function gets a PData corresponding to the username given.
     *
     * @param userName This should be the full or partial username of a player
     * @return the PData corresponding to that userName.
     */
    public PData getPData(String userName) {
        return pDataHandler.getPDataFromUsername(pDataHandler.getFullUsername(userName));
    }

    /**
     * This function searches through all loaded players and compiles a list of
     * all the data's of the type given. This list is created inside this
     * command and no references are kept.
     *
     * @param dataType This is the data type to search for.
     * @return A list of all data's of that type which are loaded.
     */
    public List<Data> getAllDatas(String dataType) {
        return pDataHandler.getAllData(dataType);
    }

    /**
     * This function returns a list of all PData's loaded, or one PData for
     * every single player who has ever joined this server. This list is an
     * UNMODIFIABLE version of PlayerData's Internal list.
     *
     * @return A list of PData's Loaded.
     */
    public List<PData> getAllPDatas() {
        return pDataHandler.getAllPDatas();
    }

    /**
     * Run this runnable after PlayerData has fully loaded. If PlayerData is
     * fully loaded currently, then run it right now. This WILL run in the
     * Bukkit Thread, not an async one.
     */
    public void runAfterLoad(Runnable r) {
        pDataHandler.runAfterLoad(r);
    }

    public void addJoinListener(PDPlayerJoinListener pdpjl) {
        playerDataMain.getEventListener().addJoinListener(pdpjl);
    }

    public void removeJoinListener(PDPlayerJoinListener pdpjl) {
        playerDataMain.getEventListener().removeJoinListener(pdpjl);
    }

    public void addLeaveListener(PDPlayerLeaveListener pdpll) {
        playerDataMain.getEventListener().addLeaveListener(pdpll);
    }

    public void removeLeaveListener(PDPlayerLeaveListener pdpll) {
        playerDataMain.getEventListener().removeLeaveListeners(pdpll);
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
    public PData getPDataFromUsername(String name) {
        return pDataHandler.getPDataFromUsername(name);
    }
}