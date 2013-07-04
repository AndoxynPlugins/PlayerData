/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.api;

import java.util.List;

/**
 *
 * @author daboross
 */
public interface PlayerData {

    /**
     * Gets the username of this PlayerData.
     *
     * @return The username of the player.
     */
    public String getUsername();

    /**
     * Gets the displayname of this PlayerData.
     *
     * @return The displayname of the player.
     */
    public String getDisplayname();

    /**
     * This function gets whether or not this player is online.
     *
     * @return Whether or not this player is online
     */
    public boolean isOnline();

    /**
     * Gets the total time spent online on this server.
     *
     * @return The total time this player has spent online in milliseconds.
     */
    public long getTimePlayed();

    /**
     * Gets an unmodifiable list of times that this player has logged in.
     *
     * @return An unmodifiable list of LoginDatas which contains all times that
     * this player has logged in.
     */
    public List<? extends LoginData> getAllLogins();

    /**
     * Gets an unmodifiable list of times that this player has logged out.
     *
     * @return An unmodifiable list of Longs which contains all times that this
     * player has logged out.
     */
    public List<Long> getAllLogouts();

    /**
     * Gets the last date that this player was online.
     *
     * @return The last time that this player was online in milliseconds.
     */
    public long getLastSeen();

    /**
     * Gets whether or not extra data with the given name is stored in this
     * PlayerData.
     *
     * @param dataName The name of the data to check for.
     * @return true if extra data is stored under the given name, false
     * otherwise.
     */
    public boolean hasExtraData(String dataName);

    /**
     * Adds the given extra data under the given name. This extra data will be
     * saved with this PlayerData and accessible until it is removed.
     * <br>This method will overwrite any other other data that was stored
     * previously under the same dataName.
     *
     * @param dataName The name to store the data under.
     * @param data The data to store under dataName.
     * @return The extra data that was stored previously under dataName if any,
     * or null if no extra data was stored under the given name before.
     */
    public String[] addExtraData(String dataName, String[] data);

    /**
     * Removes extra data stored under the given name.
     *
     * @param dataName The name of the extra data to remove.
     * @return The data that was removed, or null if no extra data was stored
     * under dataName.
     */
    public String[] removeExtraData(String dataName);

    /**
     * Gets extra data stored under the given name.
     *
     * @param dataName The name of the extra data to get.
     * @return The extra data stored under the given name, or null if not found.
     */
    public String[] getExtraData(String dataName);

    /**
     * Gets a list of all names that extra data is stored under.
     *
     * @return A list containing all names that extra data is stored under.
     */
    public String[] getExtraDataNames();
}
