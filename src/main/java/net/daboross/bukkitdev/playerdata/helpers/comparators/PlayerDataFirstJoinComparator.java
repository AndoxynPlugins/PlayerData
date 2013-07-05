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
public class PlayerDataFirstJoinComparator implements Comparator<PlayerData> {

    @Override
    public int compare(PlayerData o1, PlayerData o2) {
        return Long.compare(o1.getAllLogins().get(0).getDate(), o2.getAllLogins().get(0).getDate());
    }
}
