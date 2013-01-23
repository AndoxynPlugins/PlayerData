package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandExecutor implements CommandExecutor {

    private final Map<String, String> aliasMap = new HashMap<>();
    private final Map<String, Boolean> isConsoleMap = new HashMap<>();
    private final Map<String, String> helpList = new HashMap<>();
    private final Map<String, String> permMap = new HashMap<>();
    private PlayerData playerDataMain;

    /**
     *
     */
    protected PlayerDataCommandExecutor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
        initCommand("?", new String[]{"help"}, true, "playerdata.help", "This Command Views This Page");
        initCommand("viewinfo", new String[]{"getinfo", "i"}, true, "playerdata.viewinfo", (ColorL.ARGS + "<Player>" + ColorL.HELP + " Gets the Info That Player data has stored on a player"));
        initCommand("recreateall", new String[]{}, true, "playerdata.admin", ("This command deletes all player data and recreates it from bukkit!"));
    }

    private void initCommand(String cmd, String[] aliases, boolean isConsole, String permission, String helpString) {
        aliasMap.put(cmd, cmd);
        for (String alias : aliases) {
            aliasMap.put(alias, cmd);
        }
        isConsoleMap.put(cmd, isConsole);
        permMap.put(cmd, permission);
        helpList.put(cmd, helpString);
    }

    /**
     *
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pd")) {
            if (args.length < 1) {
                sender.sendMessage(ColorL.MAIN + "This is a base command, Please Use a sub command after it.");
                sender.sendMessage(ColorL.MAIN + "To see all possible sub commands, type " + ColorL.CMD + "/" + cmd.getName() + ColorL.SUBCMD + " ?");
                return true;
            }
            String commandName;
            if (aliasMap.containsKey(args[0].toLowerCase())) {
                commandName = aliasMap.get(args[0].toLowerCase());
                Bukkit.getServer().getLogger().log(Level.INFO, (sender.getName() + " used " + commandName));
            } else {
                sender.sendMessage(ColorL.MAIN + "The SubCommand: " + ColorL.CMD + args[0] + ColorL.MAIN + " Does not exist.");
                sender.sendMessage(ColorL.MAIN + "To see all possible sub commands, type " + ColorL.CMD + "/" + cmd.getName() + ColorL.SUBCMD + " ?");
                return true;
            }
            if (!sender.hasPermission(permMap.get(commandName))) {
                sender.sendMessage(ColorL.NOPERM + "You don't have permission to do this command!");
                return true;
            }
            boolean isConsole;
            if (isConsoleMap.containsKey(commandName)) {
                isConsole = isConsoleMap.get(commandName);
            } else {
                isConsole = false;
            }
            if (!(sender instanceof Player)) {
                if (!isConsole) {
                    sender.sendMessage(ColorL.NOPERM + "This command must be run by a player");
                    return true;
                }
            }
            if (commandName.equalsIgnoreCase("?")) {
                runHelpCommand(sender, cmd, args);
                return true;
            } else if (commandName.equalsIgnoreCase("viewinfo")) {
                runViewInfoCommand(sender, cmd, args);
            } else if (commandName.equalsIgnoreCase("recreateall")) {
                runReCreateAllCommand(sender, cmd, args);
            }
            return true;
        }
        return false;
    }

    private String getHelpMessage(String alias, String baseCommand) {
        String str = aliasMap.get(alias);
        return (ColorL.CMD + "/" + baseCommand + ColorL.SUBCMD + " " + alias + ColorL.HELP + " " + helpList.get(aliasMap.get(str)));
    }

    private void runHelpCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorL.MAIN + "List Of Possible Sub Commands:");
        for (String str : aliasMap.keySet()) {
            if (sender.hasPermission(permMap.get(aliasMap.get(str)))) {
                sender.sendMessage(getHelpMessage(str, cmd.getLabel()));
            }
        }
    }

    private void runReCreateAllCommand(CommandSender sender, Command cmd, String[] args) {
        sender.sendMessage(ColorL.MAIN + "Now Recreating All Player Data!");
        int numberLoaded = playerDataMain.getPDataHandler().createEmptyPlayerDataFilesFromBukkit();
        sender.sendMessage(ColorL.MAIN + "Player Data has loaded " + ColorL.NUMBER + numberLoaded + ColorL.MAIN + " new data files");
    }

    private void runViewInfoCommand(CommandSender sender, Command cmd, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorL.ILLEGALARGUMENT + "Must Provide A Player!");
            sender.sendMessage(getHelpMessage(args[0], cmd.getName()));
            return;
        }
        String playerName = playerDataMain.getPDataHandler().getFullUsername(args[1]);
        if (playerName == null) {
            sender.sendMessage(ColorL.ERROR + "Player: " + ColorL.ERROR_ARGS + args[1] + ColorL.ERROR + " not found!");
            return;
        }
        PData pData = playerDataMain.getPDataHandler().getPDataFromUsername(playerName);
        if (pData == null) {
            sender.sendMessage(ColorL.ERROR + "Player: " + ColorL.ERROR_ARGS + args[1] + ColorL.ERROR + " not found!");
            return;
        }
        ArrayList<String> linesToSend = new ArrayList<>();
        linesToSend.add(ColorL.MAIN + "Info Avalible For " + ColorL.NAME + pData.userName() + ColorL.MAIN + ":");
        linesToSend.add(ColorL.MAIN + "Display Name: " + ColorL.NAME + pData.nickName(true));
        if (pData.isOnline()) {
            linesToSend.add(ColorL.NAME + pData.userName() + ColorL.MAIN + " is online");
        } else {
            linesToSend.add(ColorL.NAME + pData.userName() + ColorL.MAIN + " is not online");
            linesToSend.add(ColorL.NAME + pData.userName() + ColorL.MAIN + " was last seen " + ColorL.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - pData.lastLogOut()) + ColorL.MAIN + " ago");
        }
        linesToSend.add(ColorL.MAIN + "Times logged into " + ColorL.SERVERNAME + Bukkit.getServerName() + ColorL.MAIN + ": " + ColorL.NUMBER + pData.logIns().length);
        linesToSend.add(ColorL.MAIN + "Times logged out of " + ColorL.SERVERNAME + Bukkit.getServerName() + ColorL.MAIN + ": " + ColorL.NUMBER + pData.logOuts().length);
        linesToSend.add(ColorL.MAIN + "Time Played On " + ColorL.SERVERNAME + Bukkit.getServerName() + ColorL.MAIN + ": " + ColorL.NUMBER + PlayerData.getFormattedDDate(pData.timePlayed()));
        linesToSend.add(ColorL.MAIN + "First Time On " + ColorL.SERVERNAME + Bukkit.getServerName() + ColorL.MAIN + " was  " + ColorL.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - pData.getFirstLogIn()) + ColorL.MAIN + " ago");
        linesToSend.add(ColorL.MAIN + "First Time On " + ColorL.SERVERNAME + Bukkit.getServerName() + ColorL.MAIN + " was  " + ColorL.NUMBER + new Date(pData.getFirstLogIn()));
        PDataHandler pdh = playerDataMain.getPDataHandler();
        for (Data d : pData.getData()) {
            playerDataMain.getLogger().log(Level.INFO, "Data {0}", d.getName());
            linesToSend.addAll(Arrays.asList(pdh.getDisplayData(d, false)));
        }
        sender.sendMessage(linesToSend.toArray(new String[0]));
    }
}
