package net.daboross.bukkitdev.playerdata;

import java.util.logging.Level;
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

    private final PlayerData pDataMain;

    protected PlayerDataEventListener(PlayerData main) {
        pDataMain = main;
    }

    /**
     *
     * @param evt
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent evt) {
        Runnable logInRunnable = new Runnable() {
            public void run() {
                PData pData = pDataMain.getPDataHandler().getPDataFromUsername(evt.getPlayer().getName());
                if (pData == null) {
                    pDataMain.getLogger().log(Level.INFO, "{0} Logged In For First Time", new Object[]{evt.getPlayer().getName()});
                    evt.getPlayer().performCommand("spawn");
                }
                pDataMain.getPDataHandler().logIn(evt.getPlayer());
            }
        };
        pDataMain.getPDataHandler().runAfterLoad(logInRunnable);
    }

    /**
     *
     * @param evt
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent evt) {
        Runnable logOutRunnable = new Runnable() {
            public void run() {
                pDataMain.getPDataHandler().getPData(evt.getPlayer()).loggedOut();
            }
        };
        pDataMain.getPDataHandler().runAfterLoad(logOutRunnable);
    }
}
