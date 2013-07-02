/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.commandexecutorbase;

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
