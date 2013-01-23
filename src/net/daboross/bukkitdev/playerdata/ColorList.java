package net.daboross.bukkitdev.playerdata;

import org.bukkit.ChatColor;

/**
 *
 * @author daboross
 */
public final class ColorList {

    public static final String MAIN = ChatColor.AQUA.toString();
    public static final String NAME = ChatColor.GRAY.toString();
    public static final String NUMBER = ChatColor.RED.toString();
    public static final String CMD = ChatColor.GREEN.toString();
    public static final String HELP = ChatColor.DARK_AQUA.toString();
    public static final String SUBCMD = ChatColor.AQUA.toString();
    public static final String NOPERM = ChatColor.RED.toString();
    public static final String ARGS = ChatColor.BLUE.toString();
    public static final String ILLEGALARGUMENT = ChatColor.AQUA.toString();
    public static final String ERROR = ChatColor.DARK_RED.toString();
    public static final String ERROR_ARGS = ChatColor.RED.toString();
    public static final String SERVERNAME = ChatColor.BLUE.toString();
    public static final String DATA_HANDLE_SLASH = ChatColor.GRAY.toString();
    public static final String DIVIDER = ChatColor.GRAY.toString();
    public static final String MAIN_DARK = ChatColor.DARK_GREEN.toString();
    public static final String BROADCAST = MAIN;

    public static String getBroadcastName(String name) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.RED + name + ChatColor.DARK_GRAY + "]";
    }
}
