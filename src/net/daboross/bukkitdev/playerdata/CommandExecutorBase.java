package net.daboross.bukkitdev.playerdata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public abstract class CommandExecutorBase implements CommandExecutor {

    private final Map<String, String> aliasMap = new HashMap<String, String>();
    private final Map<String, Boolean> isConsoleMap = new HashMap<String, Boolean>();
    private final Map<String, String> helpList = new HashMap<String, String>();
    private final Map<String, String[]> helpAliasMap = new HashMap<String, String[]>();
    private final Map<String, String> permMap = new HashMap<String, String>();

    private void initCommand(String cmd, String[] aliases, boolean isConsole, String permission, String helpString) {
        aliasMap.put(cmd, cmd);
        for (String alias : aliases) {
            aliasMap.put(alias, cmd);
        }
        isConsoleMap.put(cmd, isConsole);
        permMap.put(cmd, permission);
        helpList.put(cmd, helpString);
        helpAliasMap.put(cmd, aliases);
    }

    private void invalidSubCommandMessage(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ColorList.MAIN + "The SubCommand: " + ColorList.CMD + args[0] + ColorList.MAIN + " Does not exist.");
        sender.sendMessage(ColorList.MAIN + "To see all possible sub commands, type " + ColorList.CMD + "/" + label + ColorList.SUBCMD + " ?");
    }

    private void noSubCommandMessage(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ColorList.MAIN + "This is a base command, Please Use a sub command after it.");
        sender.sendMessage(ColorList.MAIN + "To see all possible sub commands, type " + ColorList.CMD + "/" + label + ColorList.SUBCMD + " ?");
    }

    private void noPermissionMessage(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorList.NOPERM + "You don't have permission to run " + ColorList.CMD + "/" + label);
        } else {
            sender.sendMessage(ColorList.NOPERM + "You don't have permission to run " + ColorList.CMD + "/" + label + ColorList.SUBCMD + args[0]);
        }
    }

    private void noConsoleMessage(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ColorList.NOPERM + "This command must be run by a player");
    }

    /**
     * This will check if the command given is a valid sub command. It will
     * display the correct messages to the player IF the command is not valid in
     * any way. This will check if the command exists, and return null if it
     * doesn't. If the command must be run by a player and the sender isn't a
     * player then this will return null. This will check if the player has
     * permission to access the command, and if they don't, this will tell them
     * they don't and return null. If none of the above, then this will return
     * the command given, aliases turned into the base command. This will run
     * the help message and return null if the sub command is "help".
     */
    public String isCommandValid(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            noSubCommandMessage(sender, cmd, label, args);
            return null;
        }
        String commandName;
        if (aliasMap.containsKey(args[0].toLowerCase())) {
            commandName = aliasMap.get(args[0].toLowerCase());
        } else {
            invalidSubCommandMessage(sender, cmd, label, args);
            return null;
        }
        if (sender instanceof Player) {
            if (!sender.hasPermission(permMap.get(commandName))) {
                noPermissionMessage(sender, cmd, label, args);
                return null;
            }
        }
        boolean isConsole;
        if (isConsoleMap.containsKey(commandName)) {
            isConsole = isConsoleMap.get(commandName);
        } else {
            isConsole = false;
        }
        if (!(sender instanceof Player)) {
            if (!isConsole) {
                noConsoleMessage(sender, cmd, label, args);
                return null;
            }
        }
        if (commandName.equalsIgnoreCase("help")) {
            runHelpCommand(sender, cmd, getSubArray(args));
            return null;
        }
        return commandName;
    }

    /**
     * This returns an array that is the given array without the first value.
     */
    private String[] getSubArray(String[] array) {
        if (array.length > 1) {
            return Arrays.asList(array).subList(1, array.length).toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    private void runHelpCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorList.MAIN + "List Of Possible Sub Commands:");
        for (String str : aliasMap.keySet()) {
            if (str.equalsIgnoreCase(aliasMap.get(str))) {
                if (sender.hasPermission(str)) {
                    sender.sendMessage(getMultipleAliasHelpMessage(str, cmd.getLabel()));
                }
            }
        }
    }

    public String getHelpMessage(String alias, String baseCommand) {
        String str = aliasMap.get(alias);
        return (ColorList.CMD + "/" + baseCommand + ColorList.SUBCMD + " " + alias + ColorList.HELP + " " + helpList.get(aliasMap.get(str)));
    }

    public String getMultipleAliasHelpMessage(String subcmd, String baseCommand) {
        String[] aliasList = helpAliasMap.get(subcmd);
        String commandList = subcmd;
        for (String str : aliasList) {
            commandList += ColorList.DIVIDER + "/" + ColorList.SUBCMD + str;
        }
        return (ColorList.CMD + "/" + baseCommand + ColorList.SUBCMD + " " + commandList + ColorList.HELP + " " + helpList.get(subcmd));
    }
}
