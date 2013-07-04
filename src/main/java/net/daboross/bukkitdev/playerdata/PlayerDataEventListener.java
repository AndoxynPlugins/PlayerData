package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.api.PlayerData;
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

    private final PlayerDataBukkit pDataMain;

    protected PlayerDataEventListener(PlayerDataBukkit main) {
        pDataMain = main;
    }

    /**
     *
     * @param evt
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent evt) {
        PlayerData pData = pDataMain.getPDataHandler().logIn(evt.getPlayer());
        PlayerDataPlayerJoinEvent pdpje = new PlayerDataPlayerJoinEvent(evt.getPlayer(), pData);
        Bukkit.getServer().getPluginManager().callEvent(pdpje);
    }

    /**
     *
     * @param evt
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent evt) {
        PlayerData pd = pDataMain.getPDataHandler().logOut(evt.getPlayer());
        PlayerDataPlayerQuitEvent pdpqe = new PlayerDataPlayerQuitEvent(evt.getPlayer(), pd);
        Bukkit.getServer().getPluginManager().callEvent(pdpqe);
    }
}
