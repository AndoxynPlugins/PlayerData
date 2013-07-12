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

import java.util.logging.Logger;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author daboross
 */
public class PlayerDataStatic {

    private static PlayerDataBukkit playerDataBukkit;

    public static PlayerDataBukkit getPlayerDataBukkit() {
        return playerDataBukkit;
    }

    public static PlayerHandler getPlayerHandler() {
        return playerDataBukkit == null ? null : playerDataBukkit.getHandler();
    }

    public static boolean isPermissionLoaded() {
        return playerDataBukkit == null ? null : playerDataBukkit.isPermissionLoaded();
    }

    public static Permission getPermissionHandler() {
        return playerDataBukkit == null ? null : playerDataBukkit.getPermissionHandler();
    }

    public static Logger getLogger() {
        return playerDataBukkit == null ? null : playerDataBukkit.getLogger();
    }

    public static int getAPIVersion() {
        return playerDataBukkit == null ? -1 : playerDataBukkit.getAPIVersion();
    }

    static void setPlayerDataBukkit(PlayerDataBukkit playerDataBukkit) {
        PlayerDataStatic.playerDataBukkit = playerDataBukkit;
    }
}
