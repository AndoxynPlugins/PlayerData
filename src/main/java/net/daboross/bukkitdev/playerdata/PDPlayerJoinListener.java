package net.daboross.bukkitdev.playerdata;

import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author daboross
 */
public interface PDPlayerJoinListener {

    public void playerJoinNotify(PlayerJoinEvent pje, PData pData);
}
