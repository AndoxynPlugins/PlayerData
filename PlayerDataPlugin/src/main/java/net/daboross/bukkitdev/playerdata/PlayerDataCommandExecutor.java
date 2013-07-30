/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.playerdata;

import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.CommandExecutorBase;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
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
