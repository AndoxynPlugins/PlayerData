/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.IPLookupCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.IPReverseLookupCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.ListPlayersFirstJoinCommandHandler;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.ListPlayersCommandReactor;
import net.daboross.bukkitdev.playerdata.subcommandhandlers.ViewInfoCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandExecutor {

    private final CommandExecutorBase commandExecutorBase;
    private final PlayerHandler playerHandler;
    private final PlayerDataBukkit playerDataBukkit;

    protected PlayerDataCommandExecutor(PlayerDataBukkit playerDataBukkit, PlayerHandler playerHandler) {
        this.playerDataBukkit = playerDataBukkit;
        this.playerHandler = playerHandler;
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
                runSaveAllCommand(sender);
            }
        }));
    }

    void registerCommand(PluginCommand command) {
        command.setExecutor(commandExecutorBase);
    }

    private void runSaveAllCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.REG + "Saving data");
        Bukkit.getScheduler().runTaskAsynchronously(playerDataBukkit, new Runnable() {
            @Override
            public void run() {
                playerHandler.saveAllData();
                sender.sendMessage(ColorList.REG + "Done saving data");
            }
        });
    }
}
