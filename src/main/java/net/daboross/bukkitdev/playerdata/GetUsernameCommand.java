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

import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author daboross
 */
public class GetUsernameCommand implements CommandExecutor {

    private final PlayerHandler playerHandler;

    protected GetUsernameCommand(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            String partialName = ArrayHelpers.combinedWithSeperator(args, " ");
            List<? extends PlayerData> playerDataList = playerHandler.getAllPlayerDatas();
            sender.sendMessage(String.format(ColorList.TOP_FORMAT, "AutoCompletes for " + ColorList.NAME + partialName));
            for (int numSent = 0, i = 0; i < playerDataList.size() && numSent < 10; i++) {
                PlayerData pd = playerDataList.get(i);
                if (pd.getUsername().equals(pd.getDisplayName())) {
                    if (StringUtils.containsIgnoreCase(pd.getUsername(), partialName)) {
                        sender.sendMessage(ColorList.NAME + pd.getUsername());
                        numSent++;
                    }
                } else {
                    if (StringUtils.containsIgnoreCase(pd.getUsername(), partialName) || StringUtils.containsIgnoreCase(ChatColor.stripColor(pd.getDisplayName()), partialName)) {
                        sender.sendMessage(ColorList.NAME + pd.getUsername() + ColorList.DIVIDER + " | " + ColorList.NAME + pd.getDisplayName());
                        numSent++;
                    }
                }
            }
        } else {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(ColorList.CMD + "/" + label + ColorList.ARGS + " <Partial Name>");
        }
        return true;
    }
}
