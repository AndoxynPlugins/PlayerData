/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.helpers;

import net.daboross.bukkitdev.playerdata.PlayerDataStatic;

/**
 *
 * @author daboross
 */
public class PermissionsHelper {

    public static boolean userInGroup(String username, String groupname) {
        if (!PlayerDataStatic.isPermissionLoaded()) {
            throw new IllegalStateException("Permission handler not found!");
        }
        return PlayerDataStatic.getPermissionHandler().playerInGroup((String) null, username, groupname);
    }
}
