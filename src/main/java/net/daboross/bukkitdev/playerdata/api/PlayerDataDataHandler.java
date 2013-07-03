/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.api;

/**
 *
 * @author daboross
 */
public interface PlayerDataDataHandler {

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
     * @return The PlayerData in the database for that username, or null if not
     * found.
     */
    public PlayerData getPlayerData(String username);

    /**
     * Gets a PlayerData in the database from the given partial username or
     * displayname. This function is more efficient than using
     * getPlayerData(getFullUsername(name)), but it does the exact same thing.
     *
     * @param name The partial username or displayname of the player
     * @return The first PlayerData in the database who's username or
     * displayname contains the given name, or null if not found.
     */
    public PlayerData getPlayerDataPartial(String partialName);

    /**
     * Gets a copy of the internal list of all PlayerDatas. List is in order of
     * last seen.
     *
     * @return A copy of the full list of all PlayerDatas.
     */
    public PlayerData[] getAllPlayerData();
}
