package net.daboross.bukkitdev.playerdata;

import java.util.concurrent.Callable;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.commandreactors.IPLookupCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.IPReverseLookupCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.ListPlayersByFirstJoinCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.ListPlayersCommandReactor;
import net.daboross.bukkitdev.playerdata.commandreactors.ViewInfoCommandReactor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandExecutor {

    private final CommandExecutorBase commandExecutorBase;
    private final PlayerData playerDataMain;

    protected PlayerDataCommandExecutor(final PlayerData playerDataMain) {
        this.playerDataMain = playerDataMain;
        this.commandExecutorBase = new CommandExecutorBase("playerdata.help");
        commandExecutorBase.addSubCommand(new SubCommand("viewinfo", new String[]{"getinfo", "i"}, true, "playerdata.viewinfo", new String[]{"Player"},
                "Get info on a player", new ViewInfoCommandReactor(playerDataMain)));
        commandExecutorBase.addSubCommand(new SubCommand("list", new String[]{"lp", "pl", "l"}, true, "playerdata.list", new String[]{"PageNumber"},
                "Lists all players who have ever joined this server in order of last seen", new ListPlayersCommandReactor(playerDataMain)));
        commandExecutorBase.addSubCommand(new SubCommand("listfirst", new String[]{"lf", "fl"}, true, "playerdata.firstjoinlist", new String[]{"PageNumber"},
                "List allplayers who have have ever joined this server in order of first join", new ListPlayersByFirstJoinCommandReactor(playerDataMain)));
        commandExecutorBase.addSubCommand(new SubCommand("iplookup", new String[]{"ipl", "ip"}, true, "playerdata.iplookup", new String[]{"Player"},
                "Gets all different IPs used by a Player", new IPLookupCommandReactor(playerDataMain)));
        commandExecutorBase.addSubCommand(new SubCommand("ipreverselookup", new String[]{"ipr", "iprl"}, true, "playerdata.ipreverselookup", new String[]{"IP"},
                "Gets all different Players using an IP", new IPReverseLookupCommandReactor(playerDataMain)));
        commandExecutorBase.addSubCommand(new SubCommand("save-all", true, "playerdata.admin", "Save All PlayerDatas", new SubCommandHandler() {
            @Override
            public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                runXMLCommand(sender);
            }
        }));
        commandExecutorBase.addSubCommand(new SubCommand("recreateall", true, "playerdata.admin", "Deletes all player data and recreates it from bukkit", new SubCommandHandler() {
            @Override
            public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
                runReCreateAllCommand(sender, baseCommandLabel, subCommandLabel, subCommandArgs);
            }
        }));
    }

    protected void registerCommand(PluginCommand command) {
        command.setExecutor(commandExecutorBase);
    }

    private void runXMLCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.MAIN + "Saving PlayerDatas");
        playerDataMain.getPDataHandler().saveAllData(true, new Callable<Void>() {
            @Override
            public Void call() {
                sender.sendMessage(ColorList.MAIN + "PlayerData Saving Done");
                return null;
            }
        });
    }

    private void runReCreateAllCommand(CommandSender sender, String baseCommandLabel, String subCommandLabel, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ColorList.MAIN + "Arguments aren't needed after " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel);
            return;
        }
        sender.sendMessage(ColorList.MAIN + "Now Recreating All Player Data!");
        int numberLoaded = playerDataMain.getPDataHandler().createEmptyPlayerDataFilesFromBukkit();
        sender.sendMessage(ColorList.MAIN + "Player Data has loaded " + ColorList.NUMBER + numberLoaded + ColorList.MAIN + " new data files");
    }
}
