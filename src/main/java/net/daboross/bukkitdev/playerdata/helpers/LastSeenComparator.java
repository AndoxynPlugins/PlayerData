/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.helpers;

import java.util.Comparator;
import net.daboross.bukkitdev.playerdata.api.PlayerData;

/**
 *
 * @author daboross
 */
public class LastSeenComparator implements Comparator<PlayerData> {

    private static final Object INSTANCE_LOCK = new Object();
    private static LastSeenComparator instance;

    private LastSeenComparator() {
    }

    @Override
    public int compare(PlayerData o1, PlayerData o2) {
        Long l1;
        Long l2;
        if (o1.isOnline()) {
            l1 = System.currentTimeMillis();
        } else {
            l1 = o1.getLastSeen();
        }
        if (o2.isOnline()) {
            l2 = System.currentTimeMillis();
        } else {
            l2 = o2.getLastSeen();
        }
        return l2.compareTo(l1);
    }

    public static LastSeenComparator getInstance() {
        synchronized (INSTANCE_LOCK) {
            if (instance == null) {
                instance = new LastSeenComparator();
            }
            return instance;
        }
    }
}
