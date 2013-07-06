/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.api;

import java.util.List;
import net.daboross.bukkitdev.playerdata.PlayerDataBukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public interface PlayerHandler {

    /**
     * Gets the username of a player based off of the partial username or
     * partial displayname. This function only works for players inside
     * PlayerData's database.
     *
     * @returns The username of the first player found who's displayname or
     * username contains the given name, or null if not found.
     */
    public String getFullUsername(String name);

    /**
     * Gets a PlayerData in the database from the given username.
     *
     * @param username The full username of a player in the database.
     * @return The PlayerData in the database for the given username, or null if
     * not found.
     */
    public PlayerData getPlayerData(String username);

    /**
     * Gets a PlayerData in the database from the given player. Under normal
     * circumstances this method will never return null.
     *
     * @param player The full username of a player in the database.
     * @return The PlayerData in the database for the given player.
     */
    public PlayerData getPlayerData(Player player);

    /**
     * Gets a PlayerData in the database from the given partial username or
     * displayname. This function is more efficient than using
     * getPlayerData(getFullUsername(name)), but it does the exact same thing.
     *
     * @param partialName The partial username or displayname of the player
     * @return The first PlayerData in the database who's username or
     * displayname contains the given name, or null if not found.
     */
    public PlayerData getPlayerDataPartial(String partialName);

    /**
     * Gets a copy of the internal list of all PlayerDatas. List is in order of
     * last seen. Do not query this list outside of the server thread as it is
     * not thread safe. This list is in order of last seen.
     *
     * @return A copy of the full list of all PlayerDatas.
     */
    public List<? extends PlayerData> getAllPlayerDatas();

    /**
     * Gets a copy of the internal list of all PlayerDatas. List is in order of
     * last seen. Do not query this list outside of the server thread as it is
     * not thread safe. This list is in order of first seen on the server.
     *
     * @return A copy of the full list of all PlayerDatas.
     */
    public List<? extends PlayerData> getAllPlayerDatasFirstJoin();

    /**
     * Gets a list of all PlayerDatas that have an extra data stored under the
     * given dataName
     *
     * @param dataName The name of the extra data to search for.
     * @return A list (newly created - no references kept) of all PlayerDatas
     * loaded which have extra data stored under the given dataName.
     */
    public List<? extends PlayerData> getAllPlayerDatasWithExtraData(String dataName);

    /**
     * Saves all PlayerDatas to files in the current thread. This method is
     * thread safe and can be run from any thread. Usually this method is not
     * required as PlayerData automatically saves PlayerDatas to file when they
     * log out.
     */
    public void saveAllData();

    /**
     * Gets the PlayerData plugin that created this PlayerHandler.
     *
     * @return PlayerData Bukkit plugin.
     */
    public PlayerDataBukkit getPlayerDataBukkit();
}
