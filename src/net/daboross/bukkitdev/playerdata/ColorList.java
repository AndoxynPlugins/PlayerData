package net.daboross.bukkitdev.playerdata;

import org.bukkit.ChatColor;

/**
 *
 * @author daboross
 */
public class ColorList {

    public static String MAIN = ChatColor.AQUA.toString();
    public static String NAME = ChatColor.GRAY.toString();
    public static String NUMBER = ChatColor.RED.toString();
    public static String CMD = ChatColor.GREEN.toString();
    public static String HELP = ChatColor.DARK_AQUA.toString();
    public static String SUBCMD = ChatColor.AQUA.toString();
    public static String NOPERM = ChatColor.RED.toString();
    public static String ARGS = ChatColor.BLUE.toString();
    public static String ILLEGALARGUMENT = ChatColor.AQUA.toString();
    public static String ERROR = ChatColor.DARK_RED.toString();
    public static String ERROR_ARGS = ChatColor.RED.toString();
    public static String SERVERNAME = ChatColor.BLUE.toString();
    public static String DATA_HANDLE_SLASH = ChatColor.GRAY.toString();
    public static String DIVIDER = ChatColor.GRAY.toString();
    public static String MAIN_DARK = ChatColor.DARK_GREEN.toString();
    public static String BROADCAST = ChatColor.RED.toString();

    public static String getBroadcastName(String name) {
        return ChatColor.DARK_BLUE + "[" + ChatColor.GRAY + name + ChatColor.DARK_BLUE + "]";
    }
}
