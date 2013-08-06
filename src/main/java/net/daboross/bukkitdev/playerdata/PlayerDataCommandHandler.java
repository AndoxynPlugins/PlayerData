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
import net.daboross.bukkitdev.playerdata.commands.IPLookupCommand;
import net.daboross.bukkitdev.playerdata.commands.IPReverseLookupCommand;
import net.daboross.bukkitdev.playerdata.commands.ListPlayersFirstJoinCommand;
import net.daboross.bukkitdev.playerdata.commands.ListPlayersCommand;
import net.daboross.bukkitdev.playerdata.commands.SaveAllCommand;
import net.daboross.bukkitdev.playerdata.commands.ViewInfoCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 *
 * @author daboross
 */
public final class PlayerDataCommandHandler {

    private final CommandExecutorBase commandExecutorBase;
    private final PlayerHandler playerHandler;
    private final PlayerDataBukkit playerDataPlugin;

    protected PlayerDataCommandHandler(PlayerDataBukkit playerDataBukkit, PlayerHandler playerHandler) {
        this.playerDataPlugin = playerDataBukkit;
        this.playerHandler = playerHandler;
        this.commandExecutorBase = new CommandExecutorBase("playerdata.help");
        commandExecutorBase.addSubCommand(new ViewInfoCommand(playerHandler));
        commandExecutorBase.addSubCommand(new ListPlayersCommand(playerHandler));
        commandExecutorBase.addSubCommand(new ListPlayersFirstJoinCommand(playerHandler));
        commandExecutorBase.addSubCommand(new IPLookupCommand(playerHandler));
        commandExecutorBase.addSubCommand(new IPReverseLookupCommand(playerHandler));
        commandExecutorBase.addSubCommand(new SaveAllCommand(playerDataPlugin));
    }

    void registerCommand(PluginCommand command) {
        command.setExecutor(commandExecutorBase);
    }

    private void runSaveAllCommand(final CommandSender sender) {
        sender.sendMessage(ColorList.REG + "Saving data");
        Bukkit.getScheduler().runTaskAsynchronously(playerDataPlugin, new Runnable() {
            @Override
            public void run() {
                playerHandler.saveAllData();
                sender.sendMessage(ColorList.REG + "Done saving data");
            }
        });
    }
}
