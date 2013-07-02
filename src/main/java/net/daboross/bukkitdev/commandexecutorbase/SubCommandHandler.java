/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.commandexecutorbase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public interface SubCommandHandler {

    /**
     *
     * @param sender the sender initiating the command
     * @param baseCommand the base command being called
     * @param baseCommandLabel the label for the base command used by the sender
     * @param subCommand the subcommand being called
     * @param subCommandLabel the label for the subcommand being used by the
     * sender
     * @param subCommandArgs the arguments of the subcommand not including the
     * subcommand.
     */
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, SubCommand subCommand, String subCommandLabel, String[] subCommandArgs);
}
