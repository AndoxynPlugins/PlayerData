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
package net.daboross.bukkitdev.playerdata.commands;

import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author daboross
 */
public class IPReverseLookupCommand extends SubCommand {

    private final PlayerHandler playerHandler;

    public IPReverseLookupCommand(PlayerHandler playerHandler) {
        super("ipreverselookup", true, "playerdata.ipreverselookup", "Gets all different Players using an IP");
        addAliases("ipr", "iprl");
        addArgumentNames("IP");
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, ColorList.ERR + "Please specify an IP"));
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, ColorList.ERR + "Too many arguments"));
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        List<String> usersList = new ArrayList<String>();
        for (PlayerData player : playerHandler.getAllPlayerDatas()) {
            for (LoginData login : player.getAllLogins()) {
                String ip = login.getIP();
                String[] ipSplit = ip.split(":")[0].split("/");
                ip = ipSplit[ipSplit.length - 1];
                if (ip.equalsIgnoreCase(subCommandArgs[0])) {
                    usersList.add(player.getUsername());
                    break;
                }
            }
        }
        if (usersList.isEmpty()) {
            sender.sendMessage(ColorList.ERR + "No players found who have used the IP " + ColorList.ERR_ARGS + subCommandArgs[0]);
        } else {
            StringBuilder builder = new StringBuilder(usersList.get(0));
            for (int i = 1; i < usersList.size(); i++) {
                builder.append(", ").append(usersList.get(i));
            }
            sender.sendMessage(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "players who have used the IP '" + ColorList.DATA + subCommandArgs[0] + ColorList.TOP + "'" + ColorList.TOP_SEPERATOR + " --");
            sender.sendMessage(builder.toString());
        }
    }
}
