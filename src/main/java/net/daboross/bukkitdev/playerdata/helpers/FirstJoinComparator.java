/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.helpers;

import java.util.Comparator;
import net.daboross.bukkitdev.playerdata.PData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;

/**
 *
 * @author daboross
 */
public class FirstJoinComparator implements Comparator<PlayerData> {

    private static final Object INSTANCE_LOCK = new Object();
    private static FirstJoinComparator instance;

    private FirstJoinComparator() {
    }

    @Override
    public int compare(PlayerData o1, PlayerData o2) {
        return Long.compare(o1.getAllLogins().get(0).getDate(), o2.getAllLogins().get(0).getDate());
    }

    public static FirstJoinComparator getInstance() {
        synchronized (INSTANCE_LOCK) {
            if (instance == null) {
                instance = new FirstJoinComparator();
            }
            return instance;
        }
    }
}
