/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
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

    public static Logger getPlayerDataLogger() {
        return playerDataBukkit == null ? null : playerDataBukkit.getLogger();
    }

    public static PlayerHandlerImpl getInternalHandler() {
        return playerDataBukkit == null ? null : playerDataBukkit.getPDataHandler();
    }

    static void setPlayerDataBukkit(PlayerDataBukkit playerDataBukkit) {
        PlayerDataStatic.playerDataBukkit = playerDataBukkit;
    }
}
