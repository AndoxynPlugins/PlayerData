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
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.api.DateHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class ListPlayersFirstJoinCommand extends SubCommand {

    private final PlayerHandler playerHandler;

    public ListPlayersFirstJoinCommand(PlayerHandler playerHandler) {
        super("listfirst", true, "playerdata.firstjoinlist", "List allplayers who have have ever joined this server in order of first join");
        addAliases("lf", "fl");
        addArgumentNames("Page number");
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.LESS_THAN, 2, ColorList.ERR + "Too many arguments"));
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        int pageNumber;
        if (subCommandArgs.length == 0) {
            pageNumber = 1;
        } else {
            try {
                pageNumber = Integer.valueOf(subCommandArgs[0]);
            } catch (Exception e) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not an integer.");
                sender.sendMessage(this.getHelpMessage(baseCommandLabel, subCommandLabel));
                return;
            }
            if (pageNumber == 0) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a non-0 integer.");
                return;
            } else if (pageNumber < 0) {
                sender.sendMessage(ColorList.ERR_ARGS + subCommandArgs[0] + ColorList.ERR + " is not a positive integer.");
                return;
            }
        }
        int pageNumberReal = pageNumber - 1;
        List<? extends PlayerData> pDataList = playerHandler.getAllPlayerDatasFirstJoin();
        ArrayList<String> messagesToSend = new ArrayList<String>();
        messagesToSend.add(String.format(ColorList.TOP_FORMAT, " Player List " + ColorList.DATA + pageNumber + ColorList.TOP + "/" + ColorList.DATA + ((pDataList.size() / 6) + (pDataList.size() % 6 == 0 ? 0 : 1))));
        for (int i = (pageNumberReal * 6); i < ((pageNumberReal + 1) * 6) && i < pDataList.size(); i++) {
            PlayerData current = pDataList.get(i);
            messagesToSend.add(ColorList.NAME + current.getUsername() + ColorList.REG + " was first seen " + ColorList.DATA + DateHelper.relativeFormat(System.currentTimeMillis() - current.getAllLogins().get(0).getDate()) + ColorList.REG + " ago.");
        }
        if (pageNumberReal + 1 < (pDataList.size() / 6.0)) {
            messagesToSend.add(ColorList.REG + "To view the next page type '" + ColorList.CMD + "/" + baseCommandLabel + ColorList.SUBCMD + " " + subCommandLabel + ColorList.ARGS + " " + (pageNumber + 1) + ColorList.REG + "'");
        }
        sender.sendMessage(messagesToSend.toArray(new String[0]));
    }
}
