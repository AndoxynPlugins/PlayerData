/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.helpers.comparators;

import java.util.Comparator;
import net.daboross.bukkitdev.playerdata.api.LoginData;

/**
 *
 * @author daboross
 */
public class LoginDataNewestComparator implements Comparator<LoginData> {

    @Override
    public int compare(LoginData o1, LoginData o2) {
        return Long.compare(o1.getDate(), o2.getDate());
    }
}
