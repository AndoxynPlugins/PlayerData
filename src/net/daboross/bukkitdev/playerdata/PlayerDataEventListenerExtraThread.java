package net.daboross.bukkitdev.playerdata;

import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public class PlayerDataEventListenerExtraThread implements Runnable {

    private PlayerDataEventListener pdel;
    private Player p;

    public PlayerDataEventListenerExtraThread(PlayerDataEventListener pdel, Player p) {
        this.pdel = pdel;
        this.p = p;
    }

    @Override
    public void run() {
        pdel.pvpP.remove(p);
    }
}
