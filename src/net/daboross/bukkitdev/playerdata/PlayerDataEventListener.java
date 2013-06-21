package net.daboross.bukkitdev.playerdata;

import java.util.HashSet;
import java.util.Set;
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
    private final Set<PDPlayerJoinListener> joinListeners = new HashSet<PDPlayerJoinListener>();
    private final Set<PDPlayerLeaveListener> leaveListeners = new HashSet<PDPlayerLeaveListener>();

    protected void addJoinListener(PDPlayerJoinListener pdpjl) {
        joinListeners.add(pdpjl);
    }

    protected void addLeaveListener(PDPlayerLeaveListener pdpll) {
        leaveListeners.add(pdpll);
    }

    protected void removeJoinListener(PDPlayerJoinListener pdpjl) {
        joinListeners.remove(pdpjl);
    }

    protected void removeLeaveListeners(PDPlayerLeaveListener pdpll) {
        leaveListeners.remove(pdpll);
    }

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
            @Override
            public void run() {
                if (!pDataMain.getPDataHandler().logIn(evt.getPlayer())) {
                    pDataMain.getLogger().log(Level.INFO, "{0} Logged In For First Time", evt.getPlayer().getName());
                }
                for (PDPlayerJoinListener pdpjl : joinListeners) {
                    pdpjl.playerJoinNotify(evt);
                }
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
            @Override
            public void run() {
                pDataMain.getPDataHandler().logOut(evt.getPlayer());
                for (PDPlayerLeaveListener pdpll : leaveListeners) {
                    pdpll.playerLeaveNotify(evt);
                }
            }
        };
        pDataMain.getPDataHandler().runAfterLoad(logOutRunnable);
    }
}
