package net.daboross.bukkitdev.playerdata;

import java.util.concurrent.Callable;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.commandreactors.IPLookupCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.IPReverseLookupCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.ListPlayersByFirstJoinCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.ListPlayersCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.ViewInfoCommandReactor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandExecutor extends CommandExecutorBase {

    private final PlayerData playerDataMain;

    /**
     *
     */
    protected PlayerDataCommandExecutor(final PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
        initCommand("viewinfo", new String[]{"getinfo", "i"}, true, "playerdata.viewinfo", new String[]{"Player"}, "Get info on a player", new ViewInfoCommandReactor(playerDataMain));
        initCommand("recreateall", true, "playerdata.admin", "Deletes all player data and recreates it from bukkit", new CommandReactor() {
            public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs, CommandExecutorBridge executorBridge) {
                runReCreateAllCommand(sender, mainCommandLabel, subCommandLabel, subCommandArgs);
            }
        });

        initCommand("list", new String[]{"lp", "pl", "l"}, true, "playerdata.list", new String[]{"PageNumber"},
                "Lists all players who have ever joined this server in order of last seen", new ListPlayersCommandReactor(playerDataMain));
        initCommand("listfirst", new String[]{"lf", "fl"}, true, "playerdata.firstjoinlist", new String[]{"PageNumber"},
                "List allplayers who have have ever joined this server in order of first join", new ListPlayersByFirstJoinCommandReactor(playerDataMain));
        initCommand("iplookup", new String[]{"ipl", "ip"}, true, "playerdata.iplookup", new String[]{"Player"},
                "Gets all different IPs used by a Player", new IPLookupCommandReactor(playerDataMain));initCommand("ipreverselookup", new String[]{"ipr", "iprl"}, true, "playerdata.ipreverselookup", new String[]{"IP"},
                "Gets all different Players using an IP", new IPReverseLookupCommandReactor(playerDataMain));
        initCommand("xml", true, "playerdata.xml", "Save All Data As XML", new CommandReactor() {
            public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs, CommandExecutorBridge executorBridge) {
                runXMLCommand(sender);
            }
        });
        initCommand("bpd", true, "playerdata.bpd", "Save All Data AS BPD", new CommandReactor() {
            public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs, CommandExecutorBridge executorBridge) {
                runBPDCommand(sender);
            }
        });
        initCommand("loadbpd", true, "playerdata.loadbpd", "Load data from BPD PLEASE DON'T USE THIS IF YOU ARE USING XML STORAGE, IT WILL ERASE ALL XML STORAGE AND REPLACE WITH BPD DATA", new CommandReactor() {
            public void runCommand(CommandSender sender, Command mainCommand, String mainCommandLabel, String subCommand, String subCommandLabel, String[] subCommandArgs, CommandExecutorBridge executorBridge) {
                runLoadBPDCommand(sender);
            }
        });

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

    private void runLoadBPDCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.MAIN + "Loading data from BPD");
        Runnable runnable = new Runnable() {
            public void run() {
                sender.sendMessage(ColorList.MAIN + "Done Loading, you know the server is ALWAYS going to save back to XML, so the data loaded from BPD will eventually override the XML data.");
            }
        };
        playerDataMain.getPDataHandler().reReadData(runnable, false);
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

    @Override
    public String getCommandName() {
        return "pd";
    }

    @Override
    protected String getMainCmdPermission() {
        return "playerdata.help";
    }
}
