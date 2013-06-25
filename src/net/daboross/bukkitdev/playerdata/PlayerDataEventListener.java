package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.List;
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
            @Override
            public void run() {
                PData pData = pDataMain.getPDataHandler().logIn(evt.getPlayer());
                for (PDPlayerJoinListener pdpjl : joinListeners) {
                    pdpjl.playerJoinNotify(evt, pData);
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
                PData pData = pDataMain.getPDataHandler().logOut(evt.getPlayer());
                for (PDPlayerLeaveListener pdpll : leaveListeners) {
                    pdpll.playerLeaveNotify(evt, pData);
                }
            }
        };
        pDataMain.getPDataHandler().runAfterLoad(logOutRunnable);
    }
}
