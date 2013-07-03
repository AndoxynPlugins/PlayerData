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
public interface LoginData {

    /**
     * Gets the IP that was used for this login.
     *
     * @return The IP that the player used when logging in.
     */
    public String getIP();

    /**
     * Gets the date that was used for this login.
     *
     * @return A long in milliseconds that represents the date that the player
     * logged in.
     */
    public long getDate();
}
