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
public class LoginDataImpl implements LoginData {

    private final long date;
    private final String ip;

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
}
