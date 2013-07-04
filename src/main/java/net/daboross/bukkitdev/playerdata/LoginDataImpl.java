/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.api.LoginData;

/**
 *
 * @author daboross
 */
public class LoginDataImpl implements LoginData, Comparable<LoginDataImpl> {

    private long date;
    private String ip;

    /**
     * The Constructor for an LoginDataImpl. You CAN use null IP's if you don't
     * know what it is. If the IP is null it will be replaced with "Unknown".
     */
    public LoginDataImpl(final long date, final String ip) {
        this.date = date;
        this.ip = ip == null ? "Unknown" : ip;
    }

    public LoginDataImpl(long time) {
        this.date = time;
        this.ip = "Unknown";
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public int compareTo(LoginDataImpl other) {
        return Long.compare(this.date, other.date);
    }
}
