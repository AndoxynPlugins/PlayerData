/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.api.events.PlayerDataPlayerJoinEvent;
import net.daboross.bukkitdev.playerdata.api.events.PlayerDataPlayerQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author daboross
 */
public class PlayerDataEventListener implements Listener {

    private final PlayerHandlerImpl playerHandler;

    protected PlayerDataEventListener(PlayerHandlerImpl playerHandler) {
        this.playerHandler = playerHandler;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent evt) {
        PlayerData pData = playerHandler.logIn(evt.getPlayer());
        PlayerDataPlayerJoinEvent pdpje = new PlayerDataPlayerJoinEvent(evt.getPlayer(), pData);
        Bukkit.getServer().getPluginManager().callEvent(pdpje);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent evt) {
        PlayerData pd = playerHandler.logOut(evt.getPlayer());
        PlayerDataPlayerQuitEvent pdpqe = new PlayerDataPlayerQuitEvent(evt.getPlayer(), pd);
        Bukkit.getServer().getPluginManager().callEvent(pdpqe);
    }
}
