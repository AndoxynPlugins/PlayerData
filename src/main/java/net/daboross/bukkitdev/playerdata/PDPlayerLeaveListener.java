package net.daboross.bukkitdev.playerdata;

import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author daboross
 */
public interface PDPlayerLeaveListener {

    public void playerLeaveNotify(PlayerQuitEvent pqe, PData pData);
}
