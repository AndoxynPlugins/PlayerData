package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.List;
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
    private final List<PDPlayerJoinListener> joinListeners = new ArrayList<PDPlayerJoinListener>();
    private final List<PDPlayerLeaveListener> leaveListeners = new ArrayList<PDPlayerLeaveListener>();

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
            public void run() {
                if (!pDataMain.getPDataHandler().logIn(evt.getPlayer())) {
                    pDataMain.getLogger().log(Level.INFO, "{0} Logged In For First Time", new Object[]{evt.getPlayer().getName()});
                    //evt.getPlayer().performCommand("spawn");
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
            public void run() {
                pDataMain.getPDataHandler().getPData(evt.getPlayer()).loggedOut();
                for (PDPlayerLeaveListener pdpll : leaveListeners) {
                    pdpll.playerLeaveNotify(evt);
                }
            }
        };
        pDataMain.getPDataHandler().runAfterLoad(logOutRunnable);
    }
}
