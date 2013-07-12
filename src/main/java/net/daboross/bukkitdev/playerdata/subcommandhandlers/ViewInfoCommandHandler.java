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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase.SubCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.daboross.bukkitdev.playerdata.PlayerDataStatic;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.helpers.DateHelper;
import org.bukkit.Bukkit;

/**
 *
 * @author daboross
 */
public class ViewInfoCommandHandler implements SubCommandHandler {

    private final PlayerHandler playerHandler;

    public ViewInfoCommandHandler(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length < 1) {
            sender.sendMessage(ColorList.ERR + "Please specify a player");
            sender.sendMessage(subCommand.getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
        String givenPlayerName = ArrayHelpers.combinedWithSeperator(subCommandArgs, " ");
        PlayerData pd = playerHandler.getPlayerDataPartial(givenPlayerName);
        if (pd == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + givenPlayerName + ColorList.ERR + " not found");
            return;
        }
        sender.sendMessage(ColorList.TOP_SEPERATOR + " -- " + ColorList.TOP + "Info for " + ColorList.NAME + pd.getUsername() + ColorList.TOP_SEPERATOR + " --");
        ArrayList<String> linesToSend = new ArrayList<String>();
        linesToSend.add(ColorList.REG + "DisplayName: " + ColorList.NAME + pd.getDisplayname());
        if (pd.isOnline()) {
            linesToSend.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " is online");
            List<? extends LoginData> logIns = pd.getAllLogins();
            linesToSend.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " has been online " + ColorList.DATA + DateHelper.getFormattedRelativeDate(System.currentTimeMillis() - logIns.get(logIns.size() - 1).getDate()));
        } else {
            linesToSend.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " is not online");
            linesToSend.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " was last seen " + ColorList.DATA + DateHelper.getFormattedRelativeDate(System.currentTimeMillis() - pd.getLastSeen()) + ColorList.REG + " ago");
        }
        linesToSend.add(ColorList.REG + "Times logged into " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + pd.getAllLogins().size());
        linesToSend.add(ColorList.REG + "Times logged out of " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + pd.getAllLogouts().size());
        linesToSend.add(ColorList.REG + "Time played on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + ": " + ColorList.DATA + DateHelper.getFormattedRelativeDate(pd.getTimePlayed()));
        linesToSend.add(ColorList.REG + "First time on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + " was  " + ColorList.DATA + DateHelper.getFormattedRelativeDate(System.currentTimeMillis() - pd.getAllLogins().get(0).getDate()) + ColorList.REG + " ago");
        linesToSend.add(ColorList.REG + "First time on " + ColorList.SERVER + Bukkit.getServerName() + ColorList.REG + " was  " + ColorList.DATA + new Date(pd.getAllLogins().get(0).getDate()));
        if (PlayerDataStatic.isPermissionLoaded()) {
            String[] groups = PlayerDataStatic.getPermissionHandler().getPlayerGroups((String) null, pd.getUsername());
            if (groups != null) {
                linesToSend.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " is currently " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(groups, ", "));
            }
        }
        sender.sendMessage(linesToSend.toArray(new String[0]));
    }
}
