package net.daboross.bukkitdev.playerdata;

import java.util.concurrent.Callable;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
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

    @Override
    public String getCommandName() {
        return "pd";
    }

    @Override
    protected String getMainCmdPermission() {
        return "playerdata.help";
    }
}
