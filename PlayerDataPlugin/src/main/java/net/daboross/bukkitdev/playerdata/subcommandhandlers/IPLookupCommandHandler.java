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
package net.daboross.bukkitdev.playerdata.subcommandhandlers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.SubCommandHandler;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class IPLookupCommandHandler implements SubCommandHandler {

    private final PlayerHandler playerHandler;

    public IPLookupCommandHandler(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String playerNameGiven = ArrayHelpers.combinedWithSeperator(subCommandArgs, " ");
        PlayerData pd = playerHandler.getPlayerDataPartial(playerNameGiven);
        if (pd == null) {
            sender.sendMessage(ColorList.ERR + "Player '" + ColorList.ERR_ARGS + playerNameGiven + ColorList.ERR + "' not found");
            return;
        }
        List<? extends LoginData> logins = pd.getAllLogins();
        if (logins.isEmpty()) {
            sender.sendMessage(ColorList.ERR + "No know IPs for " + ColorList.ERR_ARGS + pd.getUsername());
        } else if (logins.size() == 1) {
            sender.sendMessage(ColorList.REG + "Different IPs used by " + pd.getUsername());
            sender.sendMessage(ColorList.DATA + logins.get(0).getIP());
        } else {
            Set<String> ipList = new HashSet<String>();
            for (LoginData ld : logins) {
                String ip = ld.getIP();
                String[] ipSplit = ip.split(":")[0].split("/");
                ip = ipSplit[ipSplit.length - 1];
                if (!ip.equals("Unknown")) {
                    if (!ipList.contains(ip)) {
                        ipList.add(ip);
                        sender.sendMessage(ColorList.DATA + ip);
                    }
                }
            }
        }
    }
}
