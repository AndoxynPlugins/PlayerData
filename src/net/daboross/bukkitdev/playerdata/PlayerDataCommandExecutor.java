package net.daboross.bukkitdev.playerdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandExecutor extends CommandExecutorBase {

    private PlayerData playerDataMain;

    /**
     *
     */
    protected PlayerDataCommandExecutor(PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
        initCommand("help", new String[]{"?"}, true, "playerdata.help", "Views This Page");
        initCommand("viewinfo", new String[]{"getinfo", "i"}, true, "playerdata.viewinfo", new String[]{"Player"}, "Get info on a player");
        initCommand("recreateall", true, "playerdata.admin", "Deletes all player data and recreates it from bukkit");
        initCommand("list", new String[]{"lp", "pl", "l"}, true, "playerdata.list", new String[]{"PageNumber"}, "Lists all players who have ever joined this server in order of last seen");
        initCommand("xml", true, "playerdata.xml", "Save All Data As XML");
        initCommand("bpd", true, "playerdata.bpd", "Save All Data AS BPD");
        initCommand("listfirst", new String[]{"lf", "fl"}, true, "playerdata.firstjoinlist", new String[]{"PageNumber"}, "List allplayers who have have ever joined this server in order of first join");
    }

    @Override
    public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommand.equals("viewinfo")) {
            runViewInfoCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
        } else if (subCommand.equals("recreateall")) {
            runReCreateAllCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
        } else if (subCommand.equals("list")) {
            runListCommand(sender, mainCommand, subCommandLabel, subCommandArgs);
        } else if (subCommand.equals("xml")) {
            runXMLCommand(sender);
        } else if (subCommand.equals("bpd")) {
            runBPDCommand(sender);
        } else if (subCommand.equals("listfirst")) {
            runListFirstCommand(sender, mainCommand, subCommandLabel, subCommandArgs);
        }
    }

    private void runXMLCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.MAIN + "Creating XML Files");
        playerDataMain.getPDataHandler().saveAllXML(new Callable<Void>() {
            public Void call() throws Exception {
                sender.sendMessage(ColorList.MAIN + "XML File Creation Done");
                return null;
            }
        });
    }

    private void runBPDCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.MAIN + "Creating BPD Files");
        playerDataMain.getPDataHandler().saveAllBPD(new Callable<Void>() {
            public Void call() throws Exception {
                sender.sendMessage(ColorList.MAIN + "BPD File Creation Done");
                return null;
            }
        });
    }

    private void runReCreateAllCommand(CommandSender sender, String commandLabel, String subCommandLabel, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ColorList.MAIN + "You Aren't Supposed to say anything after " + ColorList.CMD + "/" + commandLabel + " " + ColorList.SUBCMD + subCommandLabel);
            return;
        }
        sender.sendMessage(ColorList.MAIN + "Now Recreating All Player Data!");
        int numberLoaded = playerDataMain.getPDataHandler().createEmptyPlayerDataFilesFromBukkit();
        sender.sendMessage(ColorList.MAIN + "Player Data has loaded " + ColorList.NUMBER + numberLoaded + ColorList.MAIN + " new data files");
    }

    private void runViewInfoCommand(CommandSender sender, String commandLabel, String subCommandLabel, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorList.ILLEGALARGUMENT + "Must Provide A Player!");
            sender.sendMessage(getHelpMessage(subCommandLabel, commandLabel));
            return;
        }
        String playerName = playerDataMain.getPDataHandler().getFullUsername(args[0]);
        PData pData = playerDataMain.getPDataHandler().getPDataFromUsername(playerName);
        if (pData == null) {
            sender.sendMessage(ColorList.ERROR + "Player: " + ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " not found!");
            return;
        }
        sender.sendMessage(ColorList.MAIN + "Info Avalible For " + ColorList.NAME + pData.userName() + ColorList.MAIN + ":");
        ArrayList<String> linesToSend = new ArrayList<String>();
        linesToSend.add(ColorList.MAIN + "Display Name: " + ColorList.NAME + pData.nickName());
        if (pData.isOnline()) {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " is online");
            IPLogin[] logIns = pData.logIns();
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " has been online " + ColorList.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - logIns[logIns.length - 1].time()));
        } else {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " is not online");
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " was last seen " + ColorList.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - pData.lastSeen()) + ColorList.MAIN + " ago");
        }
        linesToSend.add(ColorList.MAIN + "Times logged into " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + ": " + ColorList.NUMBER + pData.logIns().length);
        linesToSend.add(ColorList.MAIN + "Times logged out of " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + ": " + ColorList.NUMBER + pData.logOuts().length);
        linesToSend.add(ColorList.MAIN + "Time Played On " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + ": " + ColorList.NUMBER + PlayerData.getFormattedDDate(pData.timePlayed()));
        linesToSend.add(ColorList.MAIN + "First Time On " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + " was  " + ColorList.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - pData.getFirstLogIn().time()) + ColorList.MAIN + " ago");
        linesToSend.add(ColorList.MAIN + "First Time On " + ColorList.SERVERNAME + Bukkit.getServerName() + ColorList.MAIN + " was  " + ColorList.NUMBER + new Date(pData.getFirstLogIn().time()));
        if (PlayerData.isPEX()) {
            linesToSend.add(ColorList.NAME + pData.userName() + ColorList.MAIN + " is currently " + ColorList.NUMBER + PlayerData.formatList(pData.getGroups()));
        }
        PDataHandler pdh = playerDataMain.getPDataHandler();
        for (Data d : pData.getData()) {
            linesToSend.addAll(Arrays.asList(pdh.getDisplayData(d, false)));
        }
        sender.sendMessage(linesToSend.toArray(new String[0]));
    }

    private void runListCommand(CommandSender sender, Command cmd, String aliasLabel, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ColorList.MAIN + "Please Use Only 1 Number After " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel);
        }
        int pageNumber;
        if (args.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(args[0]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
                return;
            }
            if (pageNumber < 1) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a non-0 positive number.");
                return;
            }
        }
        PData[] pDataList = playerDataMain.getPDataHandler().getAllPDatas();
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add("");
        messagesToSend.add(ColorList.MAIN_DARK + "Player List, Page " + ColorList.NUMBER + pageNumber + ColorList.MAIN_DARK + ":");
        for (int i = ((pageNumber - 1) * 6); i < ((pageNumber - 1) * 6) + 6 & i < pDataList.length; i++) {
            PData current = pDataList[i];
            messagesToSend.add(ColorList.NAME + current.userName() + ColorList.MAIN + " was last seen " + ColorList.NUMBER + PlayerData.getFormattedDDate(current.isOnline() ? 0 : System.currentTimeMillis() - current.lastSeen()) + ColorList.MAIN + " ago.");
        }
        if (pageNumber < (pDataList.length / 6.0)) {
            messagesToSend.add(ColorList.MAIN_DARK + "To View The Next Page, Type: " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }

    private void runListFirstCommand(CommandSender sender, Command cmd, String aliasLabel, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ColorList.MAIN + "Please Use Only 1 Number After " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel);
        }
        int pageNumber;
        if (args.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(args[0]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a number.");
                sender.sendMessage(getHelpMessage(aliasLabel, cmd.getLabel()));
                return;
            }
            if (pageNumber < 1) {
                sender.sendMessage(ColorList.ERROR_ARGS + args[0] + ColorList.ERROR + " is not a non-0 positive number.");
                return;
            }
        }
        PData[] pDataList = playerDataMain.getPDataHandler().getAllPDatasFirstJoin();
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add("");
        messagesToSend.add(ColorList.MAIN_DARK + "Player List, Page " + ColorList.NUMBER + pageNumber + ColorList.MAIN_DARK + ":");
        for (int i = ((pageNumber - 1) * 6); i < ((pageNumber - 1) * 6) + 6 & i < pDataList.length; i++) {
            PData current = pDataList[i];
            messagesToSend.add(ColorList.NAME + current.userName() + ColorList.MAIN + " was first seen " + ColorList.NUMBER + PlayerData.getFormattedDDate(System.currentTimeMillis() - current.getFirstLogIn().time()) + ColorList.MAIN + " ago.");
        }
        if (pageNumber < (pDataList.length / 6.0)) {
            messagesToSend.add(ColorList.MAIN_DARK + "To View The Next Page, Type: " + ColorList.CMD + "/" + cmd.getName() + ColorList.SUBCMD + " " + aliasLabel + ColorList.ARGS + " " + (pageNumber + 1));
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }

    @Override
    public String getCommandName() {
        return "pd";
    }
}
