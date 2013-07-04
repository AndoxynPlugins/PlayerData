/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.api.events;

import net.daboross.bukkitdev.playerdata.api.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author daboross
 */
public class PlayerDataPlayerQuitEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerData playerData;

    public PlayerDataPlayerQuitEvent(Player player, PlayerData playerData) {
        super(player);
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
