/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
