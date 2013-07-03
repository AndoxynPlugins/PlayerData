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
}
