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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author daboross
 */
public class SubCommand {

    private final Set<CommandExecutorBase> commandExecutorBasesUsingThis;
    final SubCommandHandler commandHandler;
    final String commandName;
    final boolean playerOnly;
    final String permission;
    private final List<String> aliases;
    final List<String> aliasesUnmodifiable;
    final String helpMessage;
    private final List<String> argumentNames;
    final List<String> argumentNamesUnmodifiable;
    private ArgumentHandler argumentHandler;

    public SubCommand(final String commandName, final String[] aliases, final boolean canConsoleExecute, final String permission, final String[] argumentNames, String helpMessage, SubCommandHandler subCommandHandler) {
        if (commandName == null) {
            throw new IllegalArgumentException("Null commandName argument");
        } else if (subCommandHandler == null) {
            throw new IllegalArgumentException("Null subCommandHandler argument");
        }
        this.commandName = commandName.toLowerCase(Locale.ENGLISH);
        this.aliases = aliases == null ? new ArrayList<String>() : ArrayHelpers.copyToListLowercase(aliases);
        this.aliasesUnmodifiable = Collections.unmodifiableList(this.aliases);
        this.playerOnly = !canConsoleExecute;
        this.permission = permission;
        this.helpMessage = (helpMessage == null ? "" : helpMessage);
        this.argumentNames = argumentNames == null ? new ArrayList<String>() : ArrayHelpers.copyToList(argumentNames);
        this.argumentNamesUnmodifiable = Collections.unmodifiableList(this.argumentNames);
        this.commandHandler = subCommandHandler;
        this.commandExecutorBasesUsingThis = new HashSet<CommandExecutorBase>();
        this.argumentHandler = null;
    }

    public SubCommand(String cmd, String[] aliases, boolean isConsole, String permission, String helpString, SubCommandHandler commandHandler) {
        this(cmd, aliases, isConsole, permission, null, helpString, commandHandler);
    }

    public SubCommand(String cmd, boolean isConsole, String permission, String[] arguments, String helpString, SubCommandHandler commandHandler) {
        this(cmd, null, isConsole, permission, arguments, helpString, commandHandler);
    }

    public SubCommand(String cmd, boolean isConsole, String permission, String helpString, SubCommandHandler commandHandler) {
        this(cmd, null, isConsole, permission, null, helpString, commandHandler);
    }

    public void addAlias(String alias) {
        this.aliases.add(alias);
        for (CommandExecutorBase commandExecutorBase : commandExecutorBasesUsingThis) {
            commandExecutorBase.addAlias(this, alias);
        }
    }

    public void setArgumentHandler(ArgumentHandler argumentHandler) {
        this.argumentHandler = argumentHandler;
    }

    public String getName() {
        return commandName;
    }

    public String getHelpMessage(String baseCommandLabel) {
        return CommandExecutorBase.getHelpMessage(this, baseCommandLabel);
    }

    public String getHelpMessage(String baseCommandLabel, String subCommandLabel) {
        return CommandExecutorBase.getHelpMessage(this, baseCommandLabel, subCommandLabel);
    }

    void usingCommand(CommandExecutorBase commandExecutorBase) {
        commandExecutorBasesUsingThis.add(commandExecutorBase);
    }

    ArgumentHandler getArgumentHandler() {
        return argumentHandler;
    }
}
