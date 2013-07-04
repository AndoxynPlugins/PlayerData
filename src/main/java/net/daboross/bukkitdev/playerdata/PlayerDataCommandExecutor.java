/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import java.util.concurrent.Callable;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.IPLookupCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.IPReverseLookupCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.ListPlayersFirstJoinCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.ListPlayersCommandReactor;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.ViewInfoCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandExecutor {

    private final CommandExecutorBase commandExecutorBase;
    private final PlayerHandlerImpl playerHandler;

    protected PlayerDataCommandExecutor(final PlayerHandlerImpl playerHandler) {
        this.commandExecutorBase = new CommandExecutorBase("playerdata.help");
        commandExecutorBase.addSubCommand(new SubCommand("viewinfo", new String[]{"getinfo", "i"}, true, "playerdata.viewinfo", new String[]{"Player"},
                "Get info on a player", new ViewInfoCommandHandler(playerHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("list", new String[]{"lp", "pl", "l"}, true, "playerdata.list", new String[]{"PageNumber"},
                "Lists all players who have ever joined this server in order of last seen", new ListPlayersCommandReactor(playerHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("listfirst", new String[]{"lf", "fl"}, true, "playerdata.firstjoinlist", new String[]{"PageNumber"},
                "List allplayers who have have ever joined this server in order of first join", new ListPlayersFirstJoinCommandHandler(playerHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("iplookup", new String[]{"ipl", "ip"}, true, "playerdata.iplookup", new String[]{"Player"},
                "Gets all different IPs used by a Player", new IPLookupCommandHandler(playerHandler)));
        commandExecutorBase.addSubCommand(new SubCommand("ipreverselookup", new String[]{"ipr", "iprl"}, true, "playerdata.ipreverselookup", new String[]{"IP"},
                "Gets all different Players using an IP", new IPReverseLookupCommandHandler(playerHandler)));
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
        this.playerHandler = playerHandler;
    }

    protected void registerCommand(PluginCommand command) {
        command.setExecutor(commandExecutorBase);
    }

    private void runXMLCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.REG + "Saving data");
        playerHandler.saveAllData(true, new Callable<Void>() {
            @Override
            public Void call() {
                sender.sendMessage(ColorList.REG + "Data saving done");
                return null;
            }
        });
    }

    private void runReCreateAllCommand(CommandSender sender, String baseCommandLabel, String subCommandLabel, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ColorList.REG + "Arguments aren't needed after " + ColorList.CMD + "/" + baseCommandLabel + " " + ColorList.SUBCMD + subCommandLabel);
            return;
        }
        sender.sendMessage(ColorList.REG + "Now recreating all Data!");
        int numberLoaded = playerHandler.createEmptyPlayerDataFilesFromBukkit();
        sender.sendMessage(ColorList.REG + "PlayerData has loaded " + ColorList.DATA + numberLoaded + ColorList.REG + " new data files");
    }
}
