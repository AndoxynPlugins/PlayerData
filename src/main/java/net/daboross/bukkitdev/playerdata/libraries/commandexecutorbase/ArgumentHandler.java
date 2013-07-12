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
package net.daboross.bukkitdev.playerdata.libraries.commandexecutorbase;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public interface ArgumentHandler {

    /**
     *
     * @param sender the sender initiating the command
     * @param baseCommand the base command being called
     * @param baseCommandLabel the label for the base command used by the sender
     * @param subCommand the subcommand being called
     * @param subCommandLabel the label for the subcommand being used by the
     * sender
     * @param subCommandArgs the arguments so far including the one currently
     * being typed not including the subcommand.
     * @return a list of possible completes for the given argument (the last one
     * in subCommandArgs)
     */
    public List<String> tabComplete(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs);
}
