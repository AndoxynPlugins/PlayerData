/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.helpers.comparators;

import java.util.Comparator;
import net.daboross.bukkitdev.playerdata.api.PlayerData;

/**
 *
 * @author daboross
 */
public class PlayerDataLastSeenComparator implements Comparator<PlayerData> {

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
}
