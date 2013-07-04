package net.daboross.bukkitdev.playerdata;

import java.util.List;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;

/**
 * This is the main API function for PlayerDataBukkit.
 *
 * @author daboross
 */
public class PlayerDataHandler implements PlayerHandler {

    private final PDataHandler pDataHandler;
    private final PlayerDataBukkit playerDataMain;

    /**
     * Creates A Player Data Handler given a PlayerDataBukkit
     */
    public PlayerDataHandler(PlayerDataBukkit playerDataMain) {
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
    @Override
    public String getFullUsername(String name) {
        return pDataHandler.getFullUsername(name);
    }

    /**
     * Adds a DataDisplayParser that you supply as a parser for a custom data
     * type you specify. PlayerDataBukkit will call the shortInfo() function
     * from your display parser and include the lines your parser returns
     * whenever someone uses /playerdata viewinfo for a player with this data
     * type. You will need to run this function every time your Plugin is loaded
     * because PlayerDataBukkit will not keep the DataDisplayParser after
     * unload. This will overwrite any previous DataDisplayParsers loaded with
     * this function for this Data Type.
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
     * Gets a PlayerData in the database from the given username.
     *
     * @param username The full username of a player in the database.
     * @return The PlayerData in the database for that username, or null if not
     * found.
     */
    @Override
    public PlayerData getPlayerData(String username) {
        return pDataHandler.getPDataFromUsername(username);
    }

    /**
     * Gets a PlayerData in the database from the given partial username or
     * displayname. This function is more efficient than using
     * getPlayerData(getFullUsername(name)), but it does the exact same thing.
     *
     * @param partialName The partial username or displayname of the player
     * @return The first PlayerData in the database who's username or
     * displayname contains the given name, or null if not found.
     */
    @Override
    public PData getPlayerDataPartial(String partialName) {
        return pDataHandler.getPDataFromUsername(pDataHandler.getFullUsername(partialName));
    }

    /**
     * This function searches through all loaded players and compiles a list of
     * all the data's of the type given. This list is created inside this
     * command and no references are kept.
     *
     * @param dataName This is the data type to search for.
     * @return A list of all data's of that type which are loaded.
     */
    public List<String[]> getAllDatas(String dataName) {
        return pDataHandler.getAllData(dataName);
    }

    /**
     * This function returns a list of all PData's loaded, or one PData for
     * every single player who has ever joined this server. This list is an
     * UNMODIFIABLE version of PlayerDataBukkit's Internal list.
     *
     * @return A list of PData's Loaded.
     */
    @Override
    public List<? extends PlayerData> getAllPlayerDatas() {
        return pDataHandler.getAllPDatas();
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
    public PlayerData getPDataFromUsername(String name) {
        return pDataHandler.getPDataFromUsername(name);
    }
}