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
import java.util.Date;
import java.util.List;
import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.commandexecutorbase.filters.ArgumentFilter;
import net.daboross.bukkitdev.playerdata.api.DateHelper;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.api.PlayerHandler;
import net.daboross.bukkitdev.playerdata.api.events.PlayerDataInfoEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author daboross
 */
public class ViewInfoCommand extends SubCommand {

    private final PlayerHandler playerHandler;

    public ViewInfoCommand(PlayerHandler playerHandler) {
        super("viewinfo", true, "playerdata.viewinfo", "Get info on a player");
        addAliases("getinfo", "i");
        addArgumentNames("Player");
        addCommandFilter(new ArgumentFilter(ArgumentFilter.ArgumentCondition.GREATER_THAN, 0, ColorList.ERR + "Please specify a player"));
        this.playerHandler = playerHandler;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        String givenPlayerName = ArrayHelpers.combinedWithSeperator(subCommandArgs, " ");
        PlayerData pd = playerHandler.getPlayerDataPartial(givenPlayerName);
        if (pd == null) {
            sender.sendMessage(ColorList.ERR + "Player " + ColorList.ERR_ARGS + givenPlayerName + ColorList.ERR + " not found");
            return;
        }
        sender.sendMessage(String.format(ColorList.TOP_FORMAT, "Info on " + ColorList.NAME + pd.getUsername()));
        ArrayList<String> info = new ArrayList<String>();
        info.add(ColorList.REG + "Display name: " + ColorList.NAME + pd.getDisplayName());
        if (pd.isOnline()) {
            List<? extends LoginData> logins = pd.getAllLogins();
            info.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " has been online for " + ColorList.DATA + DateHelper.relativeFormat(System.currentTimeMillis() - logins.get(logins.size() - 1).getDate()));
        } else {
            info.add(ColorList.NAME + pd.getUsername() + ColorList.REG + " was last seen " + ColorList.DATA + DateHelper.relativeFormat(System.currentTimeMillis() - pd.getLastSeen()) + ColorList.REG + " ago");
        }
        info.add(ColorList.REG + "Times logged in: " + ColorList.DATA + pd.getAllLogins().size());
        info.add(ColorList.REG + "Times logged out: " + ColorList.DATA + pd.getAllLogouts().size());
        info.add(ColorList.REG + "Time played: " + ColorList.DATA + DateHelper.relativeFormat(pd.getTimePlayed()));
        info.add(ColorList.REG + "First time online was " + ColorList.DATA + DateHelper.relativeFormat(System.currentTimeMillis() - pd.getAllLogins().get(0).getDate())
                + ColorList.REG + " ago, or " + ColorList.DATA + new Date(pd.getAllLogins().get(0).getDate()));
        if (playerHandler.getPlayerDataPlugin().isPermissionLoaded()) {
            String[] groups = playerHandler.getPlayerDataPlugin().getPermission().getPlayerGroups((String) null, pd.getUsername());
            if (groups != null) {
                info.add(ColorList.REG + "Permission Groups: " + ColorList.DATA + ArrayHelpers.combinedWithSeperator(groups, ", "));
            }
        }
        PlayerDataInfoEvent event = new PlayerDataInfoEvent(sender, pd, playerHandler);
        Bukkit.getPluginManager().callEvent(event);
        sender.sendMessage(info.toArray(new String[info.size()]));
        sender.sendMessage(event.getExtraInfoArray());
    }
}
